/*
 *
 *
 * Copyright 2018 Symphony Communication Services, LLC.
 *
 * Licensed to The Symphony Software Foundation (SSF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.symphonyoss.s2.fugue.pubsub;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.annotation.Nullable;

import org.symphonyoss.s2.common.fault.FaultAccumulator;
import org.symphonyoss.s2.common.fluent.BaseAbstractBuilder;
import org.symphonyoss.s2.fugue.FugueLifecycleComponent;
import org.symphonyoss.s2.fugue.naming.INameFactory;
import org.symphonyoss.s2.fugue.naming.TopicName;
import org.symphonyoss.s2.fugue.pipeline.IThreadSafeRetryableConsumer;

import com.google.common.collect.ImmutableList;

/**
 * Base class for subscriber managers and admin controllers.
 * 
 * @author Bruce Skingle
 *
 * @param <P> Type of payload received.
 * @param <T> Type of concrete manager, needed for fluent methods.
 */
public abstract class AbstractSubscriberBase<P, T extends AbstractSubscriberBase<P,T>> extends FugueLifecycleComponent<T>
{
  protected final INameFactory                    nameFactory_;
  protected final ImmutableList<ISubscription<P>> subscribers_;
  protected final int                             totalSubscriptionCnt_;
  
  protected AbstractSubscriberBase(Class<T> type, Builder<P,?,T> builder)
  {
    super(type);
    
    nameFactory_          = builder.nameFactory_;
    subscribers_          = ImmutableList.copyOf(builder.subscribers_);
    totalSubscriptionCnt_ = builder.totalSubscriptionCnt_;
  }

  /**
   * Builder.
   * 
   * @author Bruce Skingle
   *
   * @param <P>   Type of payload received by subscribers.
   * @param <T>   The concrete type returned by fluent methods.
   * @param <B>   The concrete type of the built object.
   */
  public static abstract class Builder<P, T extends Builder<P,T,B>, B extends AbstractSubscriberBase<P,B>> extends BaseAbstractBuilder<T,B>
  {
    protected INameFactory              nameFactory_;
    protected int                       totalSubscriptionCnt_;
    protected List<ISubscription<P>>    subscribers_ = new ArrayList<>();
    protected List<Runnable>            taskList_    = new ArrayList<>();
    
    protected Builder(Class<T> type)
    {
      super(type);
    }

    public T withNameFactory(INameFactory nameFactory)
    {
      nameFactory_ = nameFactory;
      
      return self();
    }

    protected T withSubscription(@Nullable IThreadSafeRetryableConsumer<P> consumer, Subscription subscription)
    {
      taskList_.add(() ->
      {
        Collection<TopicName> topicNames = subscription.createTopicNames(nameFactory_);
        
        subscribers_.add(new TopicSubscription<P>(
            nameFactory_,
            topicNames,
            subscription.getId(),
            consumer));
        
        totalSubscriptionCnt_ += topicNames.size();
      });
      
      return self();
    }

    protected T withSubscription(@Nullable IThreadSafeRetryableConsumer<P> consumer, String subscriptionName)
    {
      taskList_.add(() ->
      {
        subscribers_.add(new QueueSubscription<P>(subscriptionName,
            consumer));
        
        totalSubscriptionCnt_ ++;
      });
      
      return self();
    }
  
    protected T withSubscription(@Nullable IThreadSafeRetryableConsumer<P> consumer, String subscriptionId, String topicId,
        String... additionalTopicIds)
    {
      taskList_.add(() ->
      {
        Collection<TopicName> topicNames = nameFactory_.getTopicNameCollection(topicId, additionalTopicIds);
        
        subscribers_.add(new TopicSubscription<P>(
            nameFactory_,
            topicNames,
            subscriptionId, consumer));
        
        totalSubscriptionCnt_ += topicNames.size();
      });
      
      return self();
    }
  
    protected T withSubscription(@Nullable IThreadSafeRetryableConsumer<P> consumer, String subscriptionId, Collection<TopicName> topicNames)
    {
      if(topicNames.isEmpty())
        throw new IllegalArgumentException("At least one topic name is required");
      
      subscribers_.add(new TopicSubscription<P>(
          nameFactory_, topicNames, subscriptionId, consumer));
      
      totalSubscriptionCnt_ += topicNames.size();
      
      return self();
    }
    
    protected T withSubscription(@Nullable IThreadSafeRetryableConsumer<P> consumer, String subscriptionId, String[] topicIds)
    {
      if(topicIds==null || topicIds.length==0)
        throw new IllegalArgumentException("At least one topic name is required");
      
      taskList_.add(() ->
      {
        List<TopicName> topicNameList = new ArrayList<>(topicIds.length);
    
        for(String id : topicIds)
        {
          topicNameList.add(nameFactory_.getTopicName(id));
        }
        
        subscribers_.add(new TopicSubscription<P>(
            nameFactory_, topicNameList, subscriptionId, consumer));
        
        totalSubscriptionCnt_ += topicNameList.size();
      });
      
      return self();
    }

    @Override
    public void validate(FaultAccumulator faultAccumulator)
    {
      super.validate(faultAccumulator);
      
      faultAccumulator.checkNotNull(nameFactory_, "nameFactory");
      
      for(Runnable task : taskList_)
      {
        task.run();
      }
    }
  }
  
  protected List<ISubscription<P>> getSubscribers()
  {
    return subscribers_;
  }
  
  protected int getTotalSubscriptionCnt()
  {
    return totalSubscriptionCnt_;
  }
}

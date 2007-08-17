package org.apache.maven.doxia.module.fml.model;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/** Encapsulates a model of a FAQ. */
public class Faq
{
    /** An id for the FAQ. */
    private String id;

    /** The question. */
    private String question;

     /** The answer. */
    private String answer;

    /**
     * Returns the id of this FAQ.
     *
     * @return the id.
     */
    public String getId()
    {
        return id;
    }

    /**
     * Sets the id of this FAQ.
     *
     * @param newId the id to set.
     */
    public void setId( String newId )
    {
        this.id = newId;
    }

    /**
     * Returns the answer of this FAQ.
     *
     * @return the answer.
     */
    public String getAnswer()
    {
        return answer;
    }

    /**
     * Sets the answer of this FAQ.
     *
     * @param newAnswer the id to set.
     */
    public void setAnswer( String newAnswer )
    {
        this.answer = newAnswer;
    }

    /**
     * Returns the question of this FAQ.
     *
     * @return the question.
     */
    public String getQuestion()
    {
        return question;
    }

    /**
     * Sets the question of this FAQ.
     *
     * @param newQuestion the id to set.
     */
    public void setQuestion( String newQuestion )
    {
        this.question = newQuestion;
    }
}

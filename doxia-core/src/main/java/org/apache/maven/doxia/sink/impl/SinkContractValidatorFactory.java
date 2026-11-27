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
package org.apache.maven.doxia.sink.impl;

import java.util.ArrayList;
import java.util.List;

import org.apache.maven.doxia.sink.Sink;

/**
 * Creates {@link SinkContractValidator} wrappers, for use with
 * {@link org.apache.maven.doxia.parser.Parser#addSinkWrapperFactory(SinkWrapperFactory)}.
 * <p>
 * This factory is not registered automatically; it must be added explicitly to a parser.
 * It remembers the validators it created. A parser keeps a registered factory (there is no way to remove one,
 * and parsers are usually singletons), so a caller that reuses a parser calls {@link #finish()} and
 * {@link #getViolations()} after each document and then {@link #reset()}. Nothing closes the wrappers a
 * parser creates, hence {@link #finish()} is the place where unclosed elements are detected.
 * </p>
 *
 * @since 2.3.0
 */
public class SinkContractValidatorFactory implements SinkWrapperFactory {

    private final SinkContractValidator.Mode mode;

    private final List<SinkContractValidator> validators = new ArrayList<>();

    /**
     * Creates a factory for validators in {@link SinkContractValidator.Mode#FAIL_FAST fail-fast mode}.
     */
    public SinkContractValidatorFactory() {
        this(SinkContractValidator.Mode.FAIL_FAST);
    }

    /**
     * Creates a factory for validators in the given mode.
     *
     * @param mode the mode of the created validators
     */
    public SinkContractValidatorFactory(SinkContractValidator.Mode mode) {
        this.mode = mode;
    }

    @Override
    public Sink createWrapper(Sink sink) {
        SinkContractValidator validator = new SinkContractValidator(sink, mode);
        validators.add(validator);
        return validator;
    }

    /**
     * The validator sees the events as emitted by the parser, before any other wrapper changes them.
     *
     * @return the highest priority, i.e. the validator is the outermost wrapper
     */
    @Override
    public int getPriority() {
        return Integer.MAX_VALUE;
    }

    /**
     * Calls {@link SinkContractValidator#finish()} on all validators created by this factory so far.
     * In fail-fast mode the first {@link SinkContractViolationException} is thrown after all validators were finished.
     */
    public void finish() {
        SinkContractViolationException first = null;
        for (SinkContractValidator validator : validators) {
            try {
                validator.finish();
            } catch (SinkContractViolationException e) {
                if (first == null) {
                    first = e;
                }
            }
        }
        if (first != null) {
            throw first;
        }
    }

    /**
     * Forgets all validators created so far, together with their violations.
     */
    public void reset() {
        validators.clear();
    }

    /**
     * Returns the violations recorded by all validators created by this factory so far,
     * in the order of creation of the validators. In
     * {@link SinkContractValidator.Mode#COLLECT collect mode} elements which are still open are included.
     * In fail-fast mode the first violation is thrown instead, so the result is always empty.
     *
     * @return a copy of the recorded violation messages, never {@code null}
     */
    public List<String> getViolations() {
        List<String> all = new ArrayList<>();
        for (SinkContractValidator validator : validators) {
            all.addAll(validator.getViolations());
            if (mode == SinkContractValidator.Mode.COLLECT) {
                String unclosed = validator.unclosedViolation("finish");
                if (unclosed != null) {
                    all.add(unclosed);
                }
            }
        }
        return all;
    }
}

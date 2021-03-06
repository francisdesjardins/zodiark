/*
 * Copyright 2013 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.zodiark.service.db.result;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.LinkedList;

public class Expressions {
    private LinkedList<Expression> expressions;

    public Expressions() {
    }

    public LinkedList<Expression> getActions() {
        return expressions;
    }

    public void setActions(LinkedList<Expression> actions) {
        this.expressions = expressions;
    }

    @JsonProperty("action")
    public Expressions action(Expression expression) {
        expressions.add(expression);
        return this;
    }
}

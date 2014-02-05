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
package org.zodiark.service.db.util;

import org.zodiark.server.Reply;
import org.zodiark.service.db.DBError;
import org.zodiark.service.util.RestService;

public abstract class ReplyAdapter<T, U> implements RestService.Reply<T, U> {

    protected final Reply reply;

    protected ReplyAdapter(Reply reply) {
        this.reply = reply;
    }

    public void fail() {
        reply.fail(new DBError());
    }

    public void exception(Exception exception) {
        reply.fail(new DBError().exception(exception).type(DBError.TYPE.UNEXPECTED));
    }
}


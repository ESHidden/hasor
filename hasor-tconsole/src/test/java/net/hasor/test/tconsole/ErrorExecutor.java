/*
 * Copyright 2008-2009 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package net.hasor.test.tconsole;
import net.hasor.tconsole.TelCommand;
import net.hasor.tconsole.TelExecutor;

/**
 * Hello Word
 * @version : 2016年4月3日
 * @author 赵永春 (zyc@hasor.net)
 */
public class ErrorExecutor implements TelExecutor {
    @Override
    public String helpInfo() {
        return "hello help.";
    }

    @Override
    public String doCommand(TelCommand telCommand) throws Throwable {
        throw new RuntimeException("error message form test");
    }
}
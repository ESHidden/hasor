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
package net.hasor.rsf.rpc.net;
import net.hasor.rsf.InterAddress;
import net.hasor.rsf.domain.OptionInfo;
import net.hasor.rsf.domain.RequestInfo;
import net.hasor.rsf.domain.ResponseInfo;
/**
 *
 * @version : 2015年12月10日
 * @author 赵永春(zyc@hasor.net)
 */
public abstract class RsfReceivedListener implements ReceivedListener {
    @Override
    public void receivedMessage(RsfChannel rsfChannel, OptionInfo info) {
        if (info instanceof RequestInfo) {
            this.receivedMessage(rsfChannel.getTarget(), (RequestInfo) info);
            return;
        }
        if (info instanceof ResponseInfo) {
            this.receivedMessage(rsfChannel.getTarget(), (ResponseInfo) info);
            return;
        }
    }
    /**从远端收到Response消息。*/
    public abstract void receivedMessage(InterAddress form, ResponseInfo response);

    /**从远端收到RequestInfo消息。*/
    public abstract void receivedMessage(InterAddress form, RequestInfo request);
}
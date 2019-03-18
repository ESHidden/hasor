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
package net.hasor.web.invoker;
import net.hasor.core.AppContext;
import net.hasor.utils.Iterators;
import net.hasor.utils.StringUtils;
import net.hasor.web.Invoker;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.net.URL;
import java.util.*;

import static org.mockito.Matchers.*;
/**
 * @version : 2016-12-16
 * @author 赵永春 (zyc@hasor.net)
 */
public class AbstractWebTest {
    private Object singleObjectFormMap(Map headerMap, String name) {
        Object[] objectFormMap = multipleObjectFormMap(headerMap, name);
        if (objectFormMap == null || objectFormMap.length == 0) {
            return null;
        } else {
            return objectFormMap[0];
        }
    }
    private Object[] multipleObjectFormMap(Map headerMap, String name) {
        if (headerMap == null) {
            return null;
        }
        Object o = headerMap.get(name);
        if (o == null) {
            return null;
        }
        if (o instanceof List) {
            return ((List) o).toArray();
        }
        if (o.getClass().isArray()) {
            return ((Object[]) o);
        }
        return new Object[] { o };
    }
    //
    protected HttpServletRequest mockRequest(String httpMethod, URL requestURL, AppContext appContext) {
        return mockRequest(httpMethod, requestURL, appContext, null);
    }
    protected HttpServletRequest mockRequest(final String httpMethod, URL requestURL, final AppContext appContext, Cookie[] cookies) {
        //
        final HttpServletRequest request = PowerMockito.mock(HttpServletRequest.class);
        PowerMockito.when(request.getMethod()).thenReturn(httpMethod);
        //
        PowerMockito.when(request.getRequestURI()).thenReturn(requestURL.getPath());
        PowerMockito.when(request.getRequestURL()).thenReturn(new StringBuffer(requestURL.getPath()));
        PowerMockito.when(request.getQueryString()).thenReturn(requestURL.getQuery());
        PowerMockito.when(request.getContextPath()).thenReturn("");
        PowerMockito.when(request.getProtocol()).thenReturn(requestURL.getProtocol());
        PowerMockito.when(request.getLocalName()).thenReturn(requestURL.getHost());
        PowerMockito.when(request.getLocalAddr()).thenReturn("127.0.0.1");
        int port = requestURL.getPort();
        PowerMockito.when(request.getLocalPort()).thenReturn((port == -1) ? 80 : port);
        //
        cookies = cookies == null ? new Cookie[0] : cookies;
        PowerMockito.when(request.getCookies()).thenReturn(cookies);
        //
        HttpSession mockSession = PowerMockito.mock(HttpSession.class);
        PowerMockito.when(request.getSession()).thenReturn(mockSession);
        PowerMockito.when(request.getSession(anyBoolean())).thenReturn(mockSession);
        //
        PowerMockito.when(request.getHeader(anyString())).thenAnswer(new Answer<String>() {
            @Override
            public String answer(InvocationOnMock invocation) throws Throwable {
                if (appContext == null) {
                    return null;
                }
                Map headerMap = appContext.findBindingBean("http-header", Map.class);
                return (String) singleObjectFormMap(headerMap, (String) invocation.getArguments()[0]);
            }
        });
        PowerMockito.when(request.getHeaderNames()).thenAnswer(new Answer<Enumeration<String>>() {
            @Override
            public Enumeration<String> answer(InvocationOnMock invocation) throws Throwable {
                if (appContext == null) {
                    return null;
                }
                Map headerMap = appContext.findBindingBean("http-header", Map.class);
                if (headerMap == null) {
                    return null;
                }
                Set<String> keySet = headerMap.keySet();
                return Iterators.asEnumeration(keySet.iterator());
            }
        });
        PowerMockito.when(request.getHeaders(anyString())).thenAnswer(new Answer<Enumeration<String>>() {
            @Override
            public Enumeration<String> answer(InvocationOnMock invocation) throws Throwable {
                if (appContext == null) {
                    return null;
                }
                Map headerMap = appContext.findBindingBean("http-header", Map.class);
                String[] objects = (String[]) multipleObjectFormMap(headerMap, (String) invocation.getArguments()[0]);
                return Iterators.asEnumeration(Arrays.asList(objects).iterator());
            }
        });
        //
        String query = requestURL.getQuery();
        final Map<String, String[]> queryMap = new HashMap<String, String[]>();
        if (StringUtils.isNotBlank(query)) {
            Map<String, List<String>> tmpQueryMap = new HashMap<String, List<String>>();
            String[] paramArray = query.split("&");
            for (String param : paramArray) {
                String[] kv = param.split("=");
                String key = kv[0].trim();
                String value = kv[1].trim();
                List<String> strings = tmpQueryMap.get(key);
                if (strings == null) {
                    strings = new ArrayList<String>();
                    tmpQueryMap.put(key, strings);
                }
                strings.add(value);
            }
            for (String key : tmpQueryMap.keySet()) {
                queryMap.put(key, tmpQueryMap.get(key).toArray(new String[0]));
            }
        }
        PowerMockito.when(request.getParameterMap()).thenAnswer(new Answer<Map<String, String[]>>() {
            @Override
            public Map<String, String[]> answer(InvocationOnMock invocation) throws Throwable {
                return queryMap;//
            }
        });
        PowerMockito.when(request.getParameterNames()).thenAnswer(new Answer<Enumeration<String>>() {
            @Override
            public Enumeration<String> answer(InvocationOnMock invocation) throws Throwable {
                return Iterators.asEnumeration(queryMap.keySet().iterator());
            }
        });
        PowerMockito.when(request.getParameterValues(anyString())).thenAnswer(new Answer<String[]>() {
            @Override
            public String[] answer(InvocationOnMock invocation) throws Throwable {
                return (String[]) multipleObjectFormMap(queryMap, (String) invocation.getArguments()[0]);
            }
        });
        //
        final Map<String, Object> attrMap = new HashMap<String, Object>();
        PowerMockito.when(request.getAttributeNames()).thenAnswer(new Answer<Enumeration<String>>() {
            @Override
            public Enumeration<String> answer(InvocationOnMock invocation) throws Throwable {
                return Iterators.asEnumeration(attrMap.keySet().iterator());
            }
        });
        PowerMockito.when(request.getAttribute(anyString())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return attrMap.get(invocation.getArguments()[0]);
            }
        });
        PowerMockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return attrMap.put((String) invocation.getArguments()[0], invocation.getArguments()[1]);
            }
        }).when(request).setAttribute(anyString(), anyObject());
        PowerMockito.doAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return attrMap.remove((String) invocation.getArguments()[0]);
            }
        }).when(request).removeAttribute(anyString());
        //
        return request;
    }
    //
    //
    protected Invoker newInvoker(HttpServletRequest request, AppContext appContext) {
        return new InvokerSupplier(appContext, request, PowerMockito.mock(HttpServletResponse.class));
    }
    protected Invoker newInvoker(HttpServletRequest request, HttpServletResponse response, final AppContext appContext) {
        return new InvokerSupplier(appContext, request, response);
    }
}
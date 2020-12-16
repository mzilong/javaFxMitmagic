
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

package sample;

import java.io.InputStream;
import java.net.URL;

/**
 * 加载资源类
 * @author mzl
 * @Description JFXResources
 * @Data  2020/9/25 15:48
 */
public final class JFXResources {
    public static URL getResource(String path) {
        return JFXResources.class.getResource(path);
    }
    public static InputStream getResourceAsStream(String path) {
        return JFXResources.class.getResourceAsStream(path);
    }

    private JFXResources() {}
}

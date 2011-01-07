/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.karaf.shell.dev;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.gogo.commands.Option;
import org.apache.karaf.shell.console.OsgiCommandSupport;
import org.apache.karaf.shell.dev.watch.BundleWatcher;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;

import java.util.List;

@Command(scope = "dev", name = "watch", description = "Watch and Update bundles")
public class Watch extends OsgiCommandSupport {

    @Argument(index = 0, name = "urls", description = "The bundle URLs", required = false)
    String urls;

    @Option(name = "-i", aliases = {}, description = "Watch interval", required = false, multiValued = false)
    private long interval;

    @Option(name = "--start", description = "Starts watching the selcted bundles", required = false, multiValued = false)
    protected boolean start;

    @Option(name = "--stop", description = "Stops watching all bundles", required = false, multiValued = false)
    protected boolean stop;

    @Option(name = "--remove", description = "Removes bundles from the watch list", required = false, multiValued = false)
    protected boolean remove;

    @Option(name = "--list", description = "Displays the watch list", required = false, multiValued = false)
    protected boolean list;

    private BundleWatcher watcher;

    @Override
    protected Object doExecute() throws Exception {
        if (urls == null && (interval == 0 && !stop && !start && !list)) {
            System.out.println("No option specified. Bundle id/url is required.");
            return null;
        }

        if (interval > 0) { //Set the interval if exists.
            watcher.setInterval(interval);
        }

        if (list) { //List the watched bundles.
            String format = "%-40s %6s %-80s";
            System.out.println(String.format(format, "URL", "ID", "Bundle Name"));
            for (String url : watcher.getWatchURLs()) {

                List<Bundle> bundleList = watcher.getBundlesByURL(url);
                if (bundleList != null && bundleList.size() > 0) {
                    for (Bundle bundle : bundleList) {
                        System.out.println(String.format(format, url, bundle.getBundleId(), (String) bundle.getHeaders().get(Constants.BUNDLE_NAME)));
                    }
                } else {
                    System.out.println(String.format(format, url, "", ""));
                }
            }
        } else if (start) {
            watcher.start();
        } else if (stop) {
            watcher.stop();
        } else if (remove) {
            watcher.remove(urls);
        } else {
            watcher.start();
            watcher.add(urls);
        }

        return null;
    }

    public BundleWatcher getWatcher() {
        return watcher;
    }

    public void setWatcher(BundleWatcher watcher) {
        this.watcher = watcher;
    }
}








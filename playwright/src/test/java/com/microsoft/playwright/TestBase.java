/*
 * Copyright (c) Microsoft Corporation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.microsoft.playwright;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;

import java.io.IOException;

public class TestBase {
  static Server server;
  static Server httpsServer;
  static BrowserType browserType;
  static Playwright playwright;
  static Browser browser;
  static boolean isMac = Utils.getOS() == Utils.OS.MAC;
  static boolean isWindows = Utils.getOS() == Utils.OS.WINDOWS;
  static boolean headful;
  Page page;
  BrowserContext context;

  static boolean isHeadful() {
    return headful;
  }

  static boolean isChromium() {
    return "chromium".equals(browserType.name());
  }

  static boolean isWebKit() {
    return "webkit".equals(browserType.name());
  }

  static boolean isFirefox() {
    return "firefox".equals(browserType.name());
  }

  @BeforeAll
  static void launchBrowser() {
    playwright = Playwright.create();


    String browserName = System.getenv("BROWSER");
    if (browserName == null) {
      browserName = "chromium";
    }
    switch (browserName) {
      case "webkit":
        browserType = playwright.webkit();
        break;
      case "firefox":
        browserType = playwright.firefox();
        break;
      case "chromium":
        browserType = playwright.chromium();
        break;
      default:
        throw new IllegalArgumentException("Unknown browser: " + browserName);
    }
    String headfulEnv = System.getenv("HEADFUL");
    headful = headfulEnv != null && !"0".equals(headfulEnv) && !"false".equals(headfulEnv);
    BrowserType.LaunchOptions options = new BrowserType.LaunchOptions();
    options.headless = !headful;
    browser = browserType.launch(options);
  }

  @AfterAll
  static void closeBrowser() {
    browser.close();
    browser = null;
  }

  @BeforeAll
  static void startServer() throws IOException {
    server = Server.createHttp(8907);
    httpsServer = Server.createHttps(8908);
  }

  @AfterAll
  static void stopServer() throws IOException {
    server.stop();
    server = null;
    httpsServer.stop();
    httpsServer = null;
  }

  @AfterAll
  static void closePlaywright() throws Exception {
    playwright.close();
    playwright = null;
  }

  @BeforeEach
  void createContextAndPage() {
    server.reset();
    httpsServer.reset();
    context = browser.newContext();
    page = context.newPage();
  }

  @AfterEach
  void closeContext() {
    context.close();
    context = null;
    page = null;
  }
}

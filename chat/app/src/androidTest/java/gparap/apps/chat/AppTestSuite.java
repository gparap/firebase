/*
 * Copyright 2021 gparap
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package gparap.apps.chat;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

import gparap.apps.chat.ui.LoginActivityInstrumentedTest;
import gparap.apps.chat.ui.SplashActivityInstrumentedTest;
import gparap.apps.chat.ui.auth.RegisterActivityInstrumentedTest;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        MainActivityInstrumentedTest.class,
        SplashActivityInstrumentedTest.class,
        LoginActivityInstrumentedTest.class,
        RegisterActivityInstrumentedTest.class
})
public class AppTestSuite {
}

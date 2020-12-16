/*******************************************************************************
 * Copyright 2013 Alexander Casall, Manuel Mauky
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package sample;

import javafx.application.Application;
import javafx.stage.Stage;
import sample.enums.HighContrastTheme;

import java.util.Locale;


/**
 * This class has to be extended by the user to build a javafx application powered by CDI.
 *
 * @author manuel.mauky
 */
public abstract class FxApplication extends Application implements IFxApplication {

	public Stage primaryStage;

    public FxApplication() {
	}

	/**
	 * Set the local default internationalized language
	 * @param locale internationalized language
	 */
	public void setDefaultLocale(Locale locale){
		Locale.setDefault(locale);
	}

	/**
	 * Set high contrast theme
	 * @param highContrastTheme enumeration of high contrast themes
	 */
	public void setHighContrastTheme(HighContrastTheme highContrastTheme){
        System.setProperty("com.sun.javafx.highContrastTheme",highContrastTheme.getName());
	}

	/**
	 * Set the user agent stylesheet used by the whole application.
	 * @param stylesheet Application.STYLESHEET_MODENA,Application.STYLESHEET_CASPIAN
	 * @see Application
	 */
	public void setApplicationStylesheet(String stylesheet){
		Application.setUserAgentStylesheet(stylesheet);
	}

	/**
	 * This method is overridden to initialize the mvvmFX framework. Override the
	 * {@link #startFx(Stage)} method for your application entry point and startup code instead of this
	 * method.
	 */
	@Override
	public final void start(Stage primaryStage) throws Exception {
		this.primaryStage = primaryStage;
		startFx(primaryStage);
	}


	/**
	 * This method is called when the javafx application is initialized. See
	 * {@link Application#init()} for more details.
	 *
	 * Unlike the original init method in {@link Application} this method contains logic to
	 * initialize the CDI container.  For this reason this method is now final to prevent unintended overriding.
	 * 	 * Please use {@link #initFx()} for you own initialization logic.
	 *
	 */
	@Override
	public final void init() {
		initFx();
	}


	/**
	 * This method is called when the application should stop. See {@link Application#stop()} for
	 * more details.
	 *
	 * Unlike the original stop method in {@link Application} this method contains logic to release
	 * resources managed by the CDI container.  For this reason this method is now final to prevent unintended overriding.
	 * 	 * Please use {@link #stopFx()} ()} for you own initialization logic.
	 *
	 */
	@Override
	public final void stop() {
		stopFx();
	}
}

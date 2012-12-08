/*
 * Copyright (c) 2012, grossmann
 * All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 * * Neither the name of the jo-widgets.org nor the
 *   names of its contributors may be used to endorse or promote products
 *   derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL jo-widgets.org BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT
 * LIABILITY, OR TORT(INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY
 * OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
 * DAMAGE.
 */

package org.jowidgets.modeler.implementor.neo4j.service;

import java.io.File;

import org.jowidgets.cap.service.neo4j.api.GraphDBConfig;
import org.jowidgets.cap.service.neo4j.api.IGraphDBConfig;
import org.jowidgets.cap.service.neo4j.api.IGraphDBConfigBuilder;
import org.jowidgets.cap.service.neo4j.tools.GraphDbConfigWrapper;

public final class Neo4JGraphDbConfig extends GraphDbConfigWrapper {

	public Neo4JGraphDbConfig() {
		super(create());
	}

	private static IGraphDBConfig create() {
		final IGraphDBConfigBuilder builder = GraphDBConfig.builder();
		final String rootPath = getRootPath();
		if (rootPath != null) {
			builder.setGraphDbService(rootPath, true);
		}
		return builder.build();
	}

	private static String getRootPath() {
		final String userHome = System.getProperty("user.home");
		if (userHome != null) {
			return userHome + File.separator + ".implementor";
		}
		else {
			return null;
		}
	}

}

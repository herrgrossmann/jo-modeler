/*
 * Copyright (c) 2011, grossmann
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
package org.jowidgets.modeler.common.bean;

import java.util.LinkedList;
import java.util.List;

import org.jowidgets.cap.common.api.bean.IBean;
import org.jowidgets.cap.security.common.api.annotation.CreateAuthorization;
import org.jowidgets.cap.security.common.api.annotation.DeleteAuthorization;
import org.jowidgets.cap.security.common.api.annotation.ReadAuthorization;
import org.jowidgets.cap.security.common.api.annotation.UpdateAuthorization;
import org.jowidgets.modeler.common.security.ModelerAuthKeys;

@CreateAuthorization(ModelerAuthKeys.CREATE_ENTITY_MODEL_PROPERTY_MODEL_LINK)
@ReadAuthorization(ModelerAuthKeys.READ_ENTITY_MODEL_PROPERTY_MODEL_LINK)
@UpdateAuthorization(ModelerAuthKeys.UPDATE_ENTITY_MODEL_PROPERTY_MODEL_LINK)
@DeleteAuthorization(ModelerAuthKeys.DELETE_ENTITY_MODEL_PROPERTY_MODEL_LINK)
public interface IEntityModelPropertyModelLink extends IBean {

	String ENTITY_MODEL_ID_PROPERTY = "entityModelId";
	String PROPERTY_MODEL_ID_PROPERTY = "propertyModelId";

	List<String> ALL_PROPERTIES = new LinkedList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(ENTITY_MODEL_ID_PROPERTY);
			add(PROPERTY_MODEL_ID_PROPERTY);
			add(IBean.ID_PROPERTY);
			add(IBean.VERSION_PROPERTY);
		}
	};

	Long getEntityModelId();

	void setEntityModelId(Long id);

	Long getPropertyModelId();

	void setPropertyModelId(final Long id);
}
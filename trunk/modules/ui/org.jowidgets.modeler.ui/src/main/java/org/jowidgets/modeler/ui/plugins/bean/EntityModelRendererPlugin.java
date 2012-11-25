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

package org.jowidgets.modeler.ui.plugins.bean;

import org.jowidgets.cap.ui.api.bean.IBeanProxy;
import org.jowidgets.cap.ui.api.bean.IBeanProxyLabelRenderer;
import org.jowidgets.cap.ui.api.model.ILabelModel;
import org.jowidgets.cap.ui.api.plugin.IBeanProxyLabelRendererPlugin;
import org.jowidgets.cap.ui.tools.bean.BeanProxyLabelRendererWrapper;
import org.jowidgets.cap.ui.tools.model.LabelModelWrapper;
import org.jowidgets.common.image.IImageConstant;
import org.jowidgets.modeler.common.bean.IEntityModel;
import org.jowidgets.modeler.common.dto.IconDescriptor;
import org.jowidgets.modeler.ui.icons.DynamicIcon;
import org.jowidgets.plugin.api.IPluginProperties;
import org.jowidgets.util.IDecorator;

public final class EntityModelRendererPlugin implements IBeanProxyLabelRendererPlugin<IEntityModel> {

	@Override
	public IDecorator<IBeanProxyLabelRenderer<IEntityModel>> getRendererDecorator(final IPluginProperties properties) {
		return new IDecorator<IBeanProxyLabelRenderer<IEntityModel>>() {
			@Override
			public IBeanProxyLabelRenderer<IEntityModel> decorate(final IBeanProxyLabelRenderer<IEntityModel> original) {
				return new BeanProxyLabelRendererWrapper<IEntityModel>(original) {
					@Override
					public ILabelModel getLabel(final IBeanProxy<IEntityModel> bean) {
						return new LabelModelWrapper(original.getLabel(bean)) {
							@Override
							public IImageConstant getIcon() {
								final IconDescriptor iconDescriptor = bean.getBean().getIconDescriptor();
								if (iconDescriptor != null) {
									return new DynamicIcon(iconDescriptor);
								}
								else {
									return super.getIcon();
								}
							}
						};
					}
				};
			}
		};
	}

}

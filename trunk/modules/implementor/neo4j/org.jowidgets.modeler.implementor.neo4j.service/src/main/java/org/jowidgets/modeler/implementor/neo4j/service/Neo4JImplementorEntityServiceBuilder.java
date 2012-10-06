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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.criteria.CriteriaQuery;

import org.jowidgets.cap.common.api.bean.IBeanDtoDescriptor;
import org.jowidgets.cap.common.api.bean.IProperty;
import org.jowidgets.cap.common.api.service.IReaderService;
import org.jowidgets.cap.service.api.entity.IBeanEntityBluePrint;
import org.jowidgets.cap.service.api.entity.IBeanEntityLinkBluePrint;
import org.jowidgets.cap.service.jpa.api.EntityManagerContextTemplate;
import org.jowidgets.cap.service.jpa.api.EntityManagerFactoryProvider;
import org.jowidgets.cap.service.jpa.api.IEntityManagerContextTemplate;
import org.jowidgets.cap.service.jpa.tools.entity.EntityManagerProvider;
import org.jowidgets.cap.service.neo4j.tools.BeanPropertyMapNodeBean;
import org.jowidgets.cap.service.neo4j.tools.Neo4JEntityServiceBuilderWrapper;
import org.jowidgets.modeler.service.persistence.bean.EntityModel;
import org.jowidgets.modeler.service.persistence.bean.RelationModel;
import org.jowidgets.service.api.IServiceRegistry;
import org.jowidgets.util.Assert;
import org.jowidgets.util.EmptyCheck;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.RelationshipType;

public final class Neo4JImplementorEntityServiceBuilder extends Neo4JEntityServiceBuilderWrapper {

	public Neo4JImplementorEntityServiceBuilder(final IServiceRegistry registry, final String entityModelPersistenceUnitName) {
		super(registry);
		Assert.paramNotEmpty(entityModelPersistenceUnitName, "entityModelPersistenceUnitName");
		final EntityManagerFactory entityManagerFactory = EntityManagerFactoryProvider.get(entityModelPersistenceUnitName);
		final IEntityManagerContextTemplate contextTemplate = EntityManagerContextTemplate.create(entityManagerFactory);
		contextTemplate.doInEntityManagerContext(new Runnable() {
			@Override
			public void run() {
				buildInEmContext();
			}
		});
	}

	private void buildInEmContext() {
		final EntityManager em = EntityManagerProvider.get();
		final CriteriaQuery<EntityModel> query = em.getCriteriaBuilder().createQuery(EntityModel.class);
		query.from(EntityModel.class);
		for (final EntityModel entityModel : em.createQuery(query).getResultList()) {
			addEntityModel(entityModel);
		}
	}

	private void addEntityModel(final EntityModel entityModel) {
		final IBeanEntityBluePrint bp = addEntity();
		bp.setEntityId(entityModel.getName());
		bp.setBeanType(BeanPropertyMapNodeBean.class);
		bp.setBeanTypeId(entityModel.getName());
		bp.setDtoDescriptor(EntityModelDtoDescriptorBuilder.create(entityModel));
		addLinkedEntities(bp, entityModel, true);
	}

	private void addLinkedEntities(final IBeanEntityBluePrint bp, final EntityModel entityModel, final boolean createEntities) {
		for (final RelationModel relation : entityModel.getSourceEntityOfDestinationEntityRelation()) {
			addLinkedEntity(bp, entityModel, relation.getDestinationEntityModel(), relation, createEntities, false);
		}
		for (final RelationModel relation : entityModel.getDestinationEntityOfSourceEntityRelation()) {
			if (!relation.getSymmetric().booleanValue() && !EmptyCheck.isEmpty(relation.getInverseLabel())) {
				addLinkedEntity(bp, entityModel, relation.getSourceEntityModel(), relation, createEntities, true);
			}
		}
	}

	private void addLinkedEntity(
		final IBeanEntityBluePrint bp,
		final EntityModel entity,
		final EntityModel linkedEntity,
		final RelationModel relation,
		final boolean createEntities,
		final boolean inverse) {

		final String entityIdSuffix = entity.getName() + relation.getName() + linkedEntity.getName();
		final String inverseSuffix = inverse ? "INVERSE" : "";

		final String linkEntityId = "Link" + entityIdSuffix + inverseSuffix;
		final String linkedEntityId = "Linked" + entityIdSuffix + inverseSuffix;
		final String linkableEntityId = "Linkable" + entityIdSuffix + inverseSuffix;

		final DynamicRelationshipType relationship = new DynamicRelationshipType(relation);

		String linkedLabel;
		if (!inverse) {
			linkedLabel = relation.getLabel();
		}
		else {
			linkedLabel = relation.getInverseLabel();
		}

		final IBeanDtoDescriptor linkedDtoDescriptor = EntityModelDtoDescriptorBuilder.create(
				linkedEntity,
				linkedLabel,
				linkedLabel);
		final IBeanDtoDescriptor linkableDtoDescriptor = EntityModelDtoDescriptorBuilder.create(linkedEntity);
		final Collection<String> linkedProperties = getProperties(linkedDtoDescriptor);

		final IBeanEntityLinkBluePrint link = bp.addLink();
		link.setLinkEntityId(linkEntityId);
		link.setLinkBeanType(DynamicRelationshipBean.class);
		link.setLinkBeanTypeId(relationship.getName());
		link.setLinkedEntityId(linkedEntityId);
		link.setLinkableEntityId(linkableEntityId);
		link.setSymmetric(relation.getSymmetric().booleanValue());

		if (!inverse) {
			link.setSourceProperties(DynamicRelationshipBean.SOURCE_ID_PROPERTY_PREFIX
				+ ":"
				+ entity.getName()
				+ ":"
				+ relationship.getName());

			link.setDestinationProperties(DynamicRelationshipBean.DESTINATION_ID_PROPERTY_PREFIX
				+ ":"
				+ linkedEntity.getName()
				+ ":"
				+ relationship.getName());
		}
		else {
			link.setSourceProperties(DynamicRelationshipBean.DESTINATION_ID_PROPERTY_PREFIX
				+ ":"
				+ entity.getName()
				+ ":"
				+ relationship.getName());

			link.setDestinationProperties(DynamicRelationshipBean.SOURCE_ID_PROPERTY_PREFIX
				+ ":"
				+ linkedEntity.getName()
				+ ":"
				+ relationship.getName());
		}

		if (createEntities) {
			final Direction direction;
			if (relation.getSymmetric().booleanValue()) {
				direction = Direction.BOTH;
			}
			else {
				direction = inverse ? Direction.INCOMING : Direction.OUTGOING;
			}

			//create linked
			final IBeanEntityBluePrint linkedBp = addEntity();
			linkedBp.setEntityId(linkedEntityId);
			linkedBp.setBeanType(BeanPropertyMapNodeBean.class);
			linkedBp.setBeanTypeId(linkedEntity.getName());
			linkedBp.setDtoDescriptor(linkedDtoDescriptor);
			final IReaderService<Void> linkedReaderService = getServiceFactory().relatedReaderService(
					entity.getName(),
					BeanPropertyMapNodeBean.class,
					linkedEntity.getName(),
					relationship,
					direction,
					true,
					linkedProperties);
			linkedBp.setReaderService(linkedReaderService);
			addLinkedEntities(linkedBp, linkedEntity, false);

			//create linkable
			final IBeanEntityBluePrint linkableBp = addEntity();
			linkableBp.setEntityId(linkableEntityId);
			linkableBp.setBeanType(BeanPropertyMapNodeBean.class);
			linkableBp.setBeanTypeId(linkedEntity.getName());
			linkableBp.setDtoDescriptor(linkableDtoDescriptor);
			final IReaderService<Void> linkableReaderService = getServiceFactory().relatedReaderService(
					entity.getName(),
					BeanPropertyMapNodeBean.class,
					linkedEntity.getName(),
					relationship,
					direction,
					false,
					linkedProperties);
			linkableBp.setReaderService(linkableReaderService);
		}
	}

	private static Collection<String> getProperties(final IBeanDtoDescriptor descriptor) {
		final List<String> result = new LinkedList<String>();
		for (final IProperty property : descriptor.getProperties()) {
			result.add(property.getName());
		}
		return result;
	}

	private static final class DynamicRelationshipType implements RelationshipType {

		private final String sourceEntityName;
		private final String destinationEntityName;
		private final String name;

		private DynamicRelationshipType(final RelationModel relation) {
			this.sourceEntityName = relation.getSourceEntityModel().getName();
			this.destinationEntityName = relation.getDestinationEntityModel().getName();
			this.name = sourceEntityName + relation.getName() + destinationEntityName;
		}

		@Override
		public String name() {
			return name;
		}

		private String getName() {
			return name;
		}

	}
}

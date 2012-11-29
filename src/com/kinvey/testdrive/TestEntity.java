package com.kinvey.testdrive;

import java.util.Arrays;
import java.util.List;

import com.kinvey.persistence.mapping.MappedEntity;
import com.kinvey.persistence.mapping.MappedField;

public class TestEntity implements MappedEntity {
	private String objectId;	
	private String name;
	
	@Override
	public List<MappedField> getMapping() {
		return Arrays.asList(new MappedField[] { 	
				new MappedField("name", "name")
				, new MappedField("objectId", ENTITY_ID_KEY)
				});
	}

	public String getObjectId() {
		return objectId;
	}

	public void setObjectId(String id) {
		this.objectId = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
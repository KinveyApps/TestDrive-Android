## Add an App Backend

In the Kinvey console click "Add an App", enter the name "TestDrive" when prompted.

![Add an app in Console](android-testdrive/ss-4.png)

## Set up TestDrive Project

1. Download the [TestDrive](https://github.com/KinveyApps/TestDrive-Android/archive/master.zip) project.
2. In Eclipse, go to **File &rarr; Import…**
3. Click **Android &rarr; Existing Android Code into Workspace**
4. **Browse…** to set **Root Directory** to the extracted zip from step 1
5. In the **Projects** box, make sure the **KinveyTestDriveActivity** project check box is selected. Then click **Finish**.
6. Specify your app key and secret in `KinveyTestDriveActivity` constant variables
![key and secret](android-testdrive/ss-5.png)

```java
public class KinveyTestDriveActivity extends Activity {

	private static final String KINVEY_APP_KEY = "your_app_key";
	private static final String KINVEY_APP_SECRET = "your_app_secret";
	
	...
```

## Saving Data
### Creating Persistable Objects

With Kinvey's Android library you model data using any class that implements `MappedEntity` interface. The sample project comes with a `TestEntity` class that has a string `name`.

`TestObject.java` looks like this:

```java
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
```

### Saving

The following method is called in the `onClickListener` of the `save_button`. When the Activty loads, it will create a new static `TestObject` with id `"12345"`.  When the save button is clicked the following method will trigger the dummy `TestObject` to be saved in the `testObjects` collection.

```java
private void save() {
	MappedAppdata ma = mKinveyClient.mappeddata(TestEntity.class, COLLECTION_NAME);
	ma.save(testObject, new ScalarCallback<TestEntity>() {

		@Override
		public void onFailure(Throwable t) {
			String msg = String.format("Save failed%nerror: %s", t.getMessage());
			mOutput.setText(msg);
			Log.e(TAG, msg);
			showAlert(KinveyTestDriveActivity.this, "Saved failed", String.format("error:%n%s", t.getLocalizedMessage()));
		}
		
		@Override
		public void onSuccess(TestEntity object) {
			String msg = String.format("Saved worked!%nSaved: '%s'", object.getName());
			mOutput.setText(msg);
			Log.d(TAG, msg);
			
			showAlert(KinveyTestDriveActivity.this, "Saved worked!", String.format("Saved: '%s'", object.getName()));
		}
	});
}
```

* Line __2__ creates a handle to the backend collection where we'll save our object
* Lines __3__ through __21__ saves the object, and displays an alert if the save is either successful or fails

## Load Data
The `MappedAppdata` object has a `load` method as a well as a save, and this can be used to load the object by id that was just saved. 

```java
private void load() {
	MappedAppdata ma = mKinveyClient.mappeddata(TestEntity.class, COLLECTION_NAME);
	TestEntity loaded = new TestEntity();
	ma.load(loaded, testObject.getObjectId(), new ScalarCallback<TestEntity>() {

		@Override
		public void onFailure(Throwable t) {
			String msg = String.format("Load failed%nerror: %s", t.getMessage());
			mOutput.setText(msg);
			Log.e(TAG, msg);
			
			showAlert(KinveyTestDriveActivity.this, "Load worked!", String.format("error:%n%s", t.getLocalizedMessage()));
		}
		
		@Override
		public void onSuccess(TestEntity object) {
			String msg = String.format("Load worked!%nLoaded: '%s'", object.getName());
			mOutput.setText(msg);
			Log.d(TAG, msg);
			
			showAlert(KinveyTestDriveActivity.this, "Load worked!", String.format("Loaded: '%s'", object.getName()));
		}
	});
}
```

* Line __2__ creates a handle to the backend collection that has the object to load
* Line __3__ instantiates an instance of TestObject for the library to load data from the backend into
* Lines __4__ through __23__ load the object, and displays an alert if the load is either successful or fails


## Run the App
Run the sample project and you will have two buttons, one to save and one to load the object. 

![Save the test object](android-testdrive/ss-1.png)
![Load the test object](android-testdrive/ss-2.png)

## Next steps


* [Data Store Guide](/android/guides/datastore) - How to query for data, delete entities 
* [Security](/android/guides/security) - How to share data or limit read/write access  
* [User Guide](/android/guides/users) - How to add users to your app and understand authentication

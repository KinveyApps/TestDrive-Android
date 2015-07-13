## Add an App Backend

In the Kinvey console click "Add an App", enter the name "TestDrive" when prompted.


## Set up TestDrive Project

1. Download the [TestDrive](https://github.com/KinveyApps/TestDrive-Android/archive/master.zip) project.
2. Download the latest Kinvey library (zip) and extract the downloaded zip file, from: http://devcenter.kinvey.com/android/downloads

###Android Studio

1. In Android Studio, go to **File &rarr; New &rarr; Import Project**
2. **Browse** to the extracted zip from step 1, and click **OK**
3. Click **Next** and **Finish**.
4. Copy all jars in the **libs/** folder of the Kinvey Android library zip to the **lib/** folder at the root of the project
5.  Click the **play** button to start a build, if you still see compilation errors ensure the versions are correctly defined in the dependencies list


###Finally, for all IDEs

Specify your app key and secret in `TestDrive` constant variables **appKey** and **appSecret**


    public class TestDrive extends Activity {
        private String appKey="your_app_key";
        private String appSecret="your_app_secret";


Take a look at our [Getting Started](http://devcenter.kinvey.com/android/guides/getting-started) guide for more information.



## Saving Data
### Creating Persistable Objects

With Kinvey's Android library you model data using any class that extends the `GenericJson` class. The sample project comes with a `Entity` class that has a string `title`.  When this class is converted to JSON, title will be given the label "_id", which is accomplished by the attribute @Key("_id")

`Entity.java` looks like this:

```java
public class Entity extends GenericJson {

  @Key("_id")
  private String title;

  public Entity() {}
  
  public Entity(String title) {
    super();
    this.title = title;
  }
  
  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }
}
```

### Saving

The following method is called in the `onClickListener` of the `save_button`. When the button is clicked, it will create a new Entity, with the title myEntity.  The below code snippet also reveals how to set values directly, without needing to declare them as variables in the Entity.  The call to `put(Key, Value)`, in this case, will add a new field labelled `description` to the entity, with a value defined below.

After creating an entity, the Client is used to access the AppData API, where a call to `save` will persist the entity with Kinvey.  As this call is asyncronous, a callback is provided to indicate the result of the operation.



```java
Entity entity = new Entity("myEntity");
        entity.put("Description","This is a description of a dynamically-added Entity property.");                
        kinveyClient.appData("entityCollection", Entity.class).save(entity, new KinveyClientCallback<Entity>() {
            @Override
            public void onSuccess(Entity result) {
                bar.setVisibility(View.GONE);
                Toast.makeText(TestDrive.this,"Entity Saved\nTitle: " + result.getTitle()
                        + "\nDescription: " + result.get("Description"), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onFailure(Throwable error) {
                bar.setVisibility(View.GONE);
                Log.e(TAG, "AppData.save Failure", error);
                Toast.makeText(TestDrive.this, "Save All error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
```



## Load Data
The `AsyncAppData` object has a `get` method as a well as a save, and this can be used to load the object by id that was just saved. 

```java
        kinveyClient.appData("entityCollection", Entity.class).get(new Query(), new KinveyListCallback<Entity>() {
            @Override
            public void onSuccess(Entity[] result) {
                bar.setVisibility(View.GONE);
                for (Entity entity : result) {
                    Toast.makeText(TestDrive.this,"Entity Retrieved\nTitle: " + entity.getTitle()
                            + "\nDescription: " + entity.get("Description"), Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Throwable error) {
                bar.setVisibility(View.GONE);
                Log.e(TAG, "AppData.get all Failure", error);
                Toast.makeText(TestDrive.this, "Get All error: " + error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
```

This is very similar to Saving data, as the AsyncAppData API is accessed through the client.  The call to `get` supports a query, so this snippet is just sending a new empty query to retrieve all results.  Note this snippet uses a `KinveyListCallback` for async results, as a query will return a list of Entities (as opposed to save, which takes a `KinveyClientCallback` and only returns a single Entity).


## Run the App
Run the sample project and you will have 5 buttons, each showing various usages of the AsyncAppData API.  Press the buttons to see what it can do, then read the source to figure out how! 


## Next steps


* [Data Store Guide](/android/guides/datastore) - How to query for data, delete entities 
* [Security](/android/guides/security) - How to share data or limit read/write access  
* [User Guide](/android/guides/users) - How to add users to your app and understand authentication


##License


Copyright (c) 2014 Kinvey Inc.

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
in compliance with the License. You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License
is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
or implied. See the License for the specific language governing permissions and limitations under
the License.

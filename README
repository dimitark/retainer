# Retainer

An Android library that retains the data on orientation change, and destroys it when the activity/fragment is actually destroyed and not needed anymore.

## Gradle

To include the library in your project, you first need to add the maven repository in you build.gradle file which is located **in the root directory** of your project.

It should look something like:

```
allprojects {
    repositories {
        jcenter()
        maven {
            url  "http://dl.bintray.com/dimitark/maven"
        }
    }
}
```

After that you need to include the library as a dependency in the build.gradle file in your **application subdirectory**.

```
compile 'si.dime.android:retainer:0.1.0'
```


## Usage

### Bucket

The library's main component is the **Bucket**. A bucket is bound to the lifecycle of a single Activity or Fragment.
It is created only and when the user requests one, for a specific Activity/Fragment. It can be done by calling
the static **getBucket(...)** method of the Retainer class, and passing a reference of an Activity or a Fragment. The Bucket is destoyed when it's parent Activity/Fragment is destroyed. 

The only limitation this library has, is that a *Bucket* that is bound to a non-retained Fragment's lifecycle, **can be used only from the onCreateView(...) method of the Fragment's lifecycle on.** If the fragment is retained - a Bucket can be requested anytime.

```Java
    // Get the bucket that is bound to this Activity/Fragment
    bucket = Retainer.getBucket(this);
```

### DataHandlers

There are two types of DataHandlers. An **Task**, which aims to replace the **AsyncTask** and an **RxHandler** which lets you use RxJava for working with the data. 

#### Task

The **Task** is similar to the Android's **AsyncTask**. The first step in using a **Task** is implementing it's interface and registering it with the **Bucket**

```Java

// Define the Task data handler
private final Task<User> userDetailsTask = new Task<User>() {
        @Override
        public User doInBackground() {
            // This method is executed from a background thread
            // So it's safe to do some 'heavy lifting' here
            return myRestService.getUserDetails();
        }

        @Override
        public void onPostExecute(User user) {
            // This method is called after the doInBackground() finishes
            // or when you request the data and the bucket already has it
            // and it's called from the UI thread
            userTextView.setText(user.getName());
        }

        @Override
        public void onError(Throwable t) {
        	// This method is called if an uncaught exception is thrown in the doInBackground() method
            // Also called from the UI thread
            Snackbar.make(rootContentView, R.string.error_loading_data, Snackbar.LENGTH_INDEFINITE).show();
        }

        @Override
        public void destroy(User user) {
        	// This method is called when the bucket is destroyed
            user.logout();
        }
    };

// Register the data handler with the Bucket
bucket.registerTask("user_details_task", userDetailsTask);

```

#### RxHandler

The **RxHandler** lets you use **RxJava**. 

```Java

// The RxHandler
private final RxHandler rxHandler = new RxHandler(
        Observable.create(new Observable.OnSubscribe<Integer>() {
            @Override
            public void call(Subscriber<? super Integer> subscriber) {
                subscriber.onNext(0);
                subscriber.onNext(1);
                subscriber.onNext(2);
                subscriber.onNext(3);
                subscriber.onNext(4);
                subscriber.onCompleted();
            }
        }),
        new Subscriber<Integer>() {
            @Override
            public void onCompleted() {
                Log.d(App.LOG_TAG, "Received all integers!");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(App.LOG_TAG, "Error occurred while receiving the integers!");
            }

            @Override
            public void onNext(Integer integer) {
                Log.d(App.LOG_TAG, "Received the integer " + integer);
            }
        },
        new Destroyer<Integer>() {
            @Override
            public void destroy(Integer integer) {
                Log.d(App.LOG_TAG, "No need to destroy integers.");
            }

            @Override
            public void destroyCompleted() {

            }
        }
);

// Register it with the Bucket
bucket.registerRxHandler("rx_handler", rxHandler);

```

#### Requesting the data

There are a couple of methods for requesting the data

```Java

// Requests the data for the given key. 
// If the data exists, the corresponding subscriber/onPostExecute() will be
// called on the end of the event loop 
// (within the same thread that this method was called from).

boolean isCachedDataAvailable = bucket.requestData("user_details_task");

```

```Java

// Requests the data for the given key. 
// If it already exists, the subscriber/onPostExecute() will be called from within this method.
// If not - a new request will be made, and the subscriber/onPostExecute() 
// will be called once the observable calls onComplete() or onError() / the doInBackground() finishes.

boolean isCachedDataAvailable = bucket.requestImmediateData("user_details_task");

```

```Java

// Requests refreshed data for the given key.
// If the data already exists, it will be removed and the observable/doInBackground() 
// will be called once again.
// If there is a running observable/doInBackground() - it will be canceled.

bucket.requestRefreshedData("user_details_task");

```


License
=======

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
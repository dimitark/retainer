package si.dime.android.retainer.testapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import si.dime.android.retainer.Bucket;
import si.dime.android.retainer.Retainer;
import si.dime.android.retainer.handlers.Task;

public class TestAppCompatActivity extends AppCompatActivity implements LoadingIndicator, DataActivity<String> {
    // The UI components
    private Button simpleTaskButton;

    // The data returned from the simple task
    private String data;

    // The bucket
    private Bucket bucket;

    // The simple task
    private Task simpleTask = new Task<String>() {
        @Override
        public String doInBackground() {
            // Set the loading flag
            simpleTaskIsLoading = true;
            // Return the string
            return "Simple test task";
        }

        @Override
        public void onPostExecute(String s) {
            simpleTaskIsLoading = false;
            data = s;
        }

        @Override
        public void onError(Throwable e) {
            simpleTaskIsLoading = false;
            data = null;
        }

        @Override
        public void destroy(String s) {}
    };
    // A flag indicating that the data of the simple task is loading...
    public boolean simpleTaskIsLoading = false;

    /**
     * Returns the main "testing" object
     *
     * @return
     */
    @Override
    public String getData() {
        return data;
    }

     @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the UI components
        simpleTaskButton = (Button) findViewById(R.id.simple_task_button);

        // Get the bucket and register the tasks
        bucket = Retainer.getBucket(this);
        bucket.registerTask("simple_task", simpleTask);

        // Register the button listeners
        simpleTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bucket.requestData("simple_task");
            }
        });
    }

    @Override
    public boolean isDataLoading() {
        return simpleTaskIsLoading;
    }
}

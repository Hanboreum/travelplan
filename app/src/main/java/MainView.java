import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.all_in_won.R;

public class MainView extends AppCompatActivity {
    Button todo,memo,money,cal,stay,traffic,shop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mainview);
        setTitle("HOME");

        todo = findViewById(R.id.todo);
        memo = findViewById(R.id.memo);
        money = findViewById(R.id.money);
        cal = findViewById(R.id.cal);
        stay = findViewById(R.id.stay);
        traffic = findViewById(R.id.traffic);
        shop = findViewById(R.id.shop);
    }
}
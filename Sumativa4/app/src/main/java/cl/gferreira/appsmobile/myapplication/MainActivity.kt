package cl.gferreira.appsmobile.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {
    private lateinit var redStatusRadio: RadioButton
    private lateinit var yellowStatusRadio: RadioButton
    private lateinit var greenStatusRadio: RadioButton
    private lateinit var humidifyButton: Button
    private lateinit var dehumidifyButton: Button
    private lateinit var offButton: Button

    private lateinit var mqttClient: MQTTHELPER
    private var humidityValue: Int = 50
    private var deviceStatus: Int = 0

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        redStatusRadio = findViewById(R.id.id_rojo)
        yellowStatusRadio = findViewById(R.id.id_amarillo)
        greenStatusRadio = findViewById(R.id.id_verde)
        val statusRadioButtonList = listOf(redStatusRadio, yellowStatusRadio, greenStatusRadio)

        mqttClient = MQTTHELPER()
        mqttClient.subscribeToTopic(MQTTHELPER.SENSOR_TOPIC, statusRadioButtonList)

        humidifyButton = findViewById(R.id.id_on)
        dehumidifyButton = findViewById(R.id.id_off)
        offButton = findViewById(R.id.id_off)

        humidifyButton.setOnClickListener {
            mqttClient.publishMessage(MQTTHELPER.DEVICE_TOPIC, "HUMIDIFIER")
            deviceStatus = 1
        }

        dehumidifyButton.setOnClickListener {
            mqttClient.publishMessage(MQTTHELPER.DEVICE_TOPIC, "DEHUMIDIFIER")
            deviceStatus = -1
        }

        offButton.setOnClickListener {
            mqttClient.publishMessage(MQTTHELPER.DEVICE_TOPIC, "OFF")
            deviceStatus = 0
        }

        GlobalScope.launch(Dispatchers.Main) {
            deviceOperation(1000)
        }
    }

    private suspend fun deviceOperation(sleepTime: Long) {
        while (true) {
            humidityValue += 5 * deviceStatus
            if (humidityValue > 100) humidityValue = 100
            else if (humidityValue < 0) humidityValue = 0

            val humidityStatus: String = when {
                humidityValue < 15 -> "RED-"
                humidityValue < 30 -> "YELLOW-"
                humidityValue < 65 -> "GREEN"
                humidityValue < 75 -> "YELLOW+"
                else -> "RED+"
            }

            mqttClient.publishMessage(MQTTHELPER.SENSOR_TOPIC, humidityStatus)
            delay(sleepTime)
        }
    }
}
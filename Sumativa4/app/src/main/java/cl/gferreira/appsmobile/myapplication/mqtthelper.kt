package com.example.otropaquete

import android.widget.RadioButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.eclipse.paho.client.mqttv3.*

class MQTTHELPER {
    private val SERVER_URI = "tcp://mqtt.eclipseprojects.io:1883"
    private val CLIENT_ID = "italo_mqtt"

    companion object {
        const val SENSOR_TOPIC = "sensorTopic"
        const val DEVICE_TOPIC = "deviceTopic"
    }

    private lateinit var mqttClient: MqttClient

    init {
        connectToMqttBroker()
    }

    private fun connectToMqttBroker() {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                val persistence = MemoryPersistence()
                mqttClient = MqttClient(SERVER_URI, CLIENT_ID, persistence)
                val options = MqttConnectOptions()
                options.isCleanSession = true

                mqttClient.connect(options)
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        }
    }

    fun subscribeToTopic(topic: String, radioButtons: List<RadioButton>) {
        GlobalScope.launch(Dispatchers.IO) {
            try {
                mqttClient.subscribe(topic) { _, message ->
                    val payload = String(message.payload)
                    when {
                        payload.startsWith("RED") -> setRadioButtonsState(radioButtons, false, false, true)
                        payload.startsWith("YELLOW") -> setRadioButtonsState(radioButtons, false, true, false)
                        else -> setRadioButtonsState(radioButtons, true, false, false)
                    }
                    println("[$topic] Mensaje recibido: $payload")
                }
            } catch (e: MqttException) {
                e.printStackTrace()
            }
        }
    }

    // Función setRadioButtonsState permanece igual, asegúrate de tenerla implementada
    private fun setRadioButtonsState(radioButtons: List<RadioButton>, red: Boolean, yellow: Boolean, green: Boolean) {
        // Implementa esta función según tus necesidades
    }
}

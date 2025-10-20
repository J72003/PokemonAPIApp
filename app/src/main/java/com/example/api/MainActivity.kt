package com.example.pokemonapi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.codepath.asynchttpclient.AsyncHttpClient
import com.codepath.asynchttpclient.callback.JsonHttpResponseHandler
import com.squareup.picasso.Picasso
import okhttp3.Headers

class MainActivity : AppCompatActivity() {
    private lateinit var pokemonImage: ImageView
    private lateinit var pokemonName: TextView
    private lateinit var pokemonType: TextView
    private lateinit var pokemonHeight: TextView
    private lateinit var btnNewPokemon: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        pokemonImage = findViewById(R.id.pokemonImage)
        pokemonName = findViewById(R.id.pokemonName)
        pokemonType = findViewById(R.id.pokemonType)
        pokemonHeight = findViewById(R.id.pokemonHeight)
        btnNewPokemon = findViewById(R.id.btnNewPokemon)

        btnNewPokemon.setOnClickListener {
            loadPokemon()
        }

        loadPokemon()
    }

    private fun loadPokemon() {
        val randomId = (1..898).random()
        val url = "https://pokeapi.co/api/v2/pokemon/$randomId"

        Log.d("PokemonAPI", "Loading Pokemon from: $url")

        val client = AsyncHttpClient()
        client.get(url, object : JsonHttpResponseHandler() {
            override fun onSuccess(statusCode: Int, headers: Headers?, json: JsonHttpResponseHandler.JSON) {
                Log.d("PokemonAPI", "Success!")

                try {
                    val jsonObject = json.jsonObject

                    val name = jsonObject.getString("name").replaceFirstChar {
                        if (it.isLowerCase()) it.titlecase() else it.toString()
                    }

                    val spriteUrl = jsonObject.getJSONObject("sprites").getString("front_default")

                    val typeArray = jsonObject.getJSONArray("types")
                    val typeName = typeArray.getJSONObject(0)
                        .getJSONObject("type")
                        .getString("name")
                        .replaceFirstChar {
                            if (it.isLowerCase()) it.titlecase() else it.toString()
                        }

                    val height = jsonObject.getInt("height")
                    val heightInMeters = height / 10.0

                    runOnUiThread {
                        pokemonName.text = "Name: $name"
                        pokemonType.text = "Type: $typeName"
                        pokemonHeight.text = "Height: ${heightInMeters}m"

                        Picasso.get().load(spriteUrl).into(pokemonImage)
                    }
                } catch (e: Exception) {
                    Log.e("PokemonAPI", "Error parsing JSON", e)
                }
            }

            override fun onFailure(
                statusCode: Int,
                headers: Headers?,
                response: String?,
                throwable: Throwable?
            ) {
                Log.e("PokemonAPI", "Failed: $response", throwable)

                runOnUiThread {
                    pokemonName.text = "Failed to load Pokemon"
                    pokemonType.text = "Please try again"
                    pokemonHeight.text = ""
                }
            }
        })
    }
}
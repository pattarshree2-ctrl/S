package com.example.websitehub

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.EditText
import android.widget.GridView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import org.json.JSONArray
import org.json.JSONObject

private data class Website(val name: String, val url: String)

class MainActivity : AppCompatActivity() {
    private val preferences by lazy { getSharedPreferences("website_hub", MODE_PRIVATE) }
    private val sites = mutableListOf<Website>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        sites += listOf(
            Website("Google", "https://www.google.com"),
            Website("YouTube", "https://www.youtube.com"),
            Website("Wikipedia", "https://www.wikipedia.org"),
            Website("Amazon", "https://www.amazon.com"),
            Website("Flipkart", "https://www.flipkart.com")
        )
        sites += loadCustomSites()

        val grid = findViewById<GridView>(R.id.websiteGrid)
        grid.adapter = WebsiteAdapter()
        grid.setOnItemClickListener { _, _, position, _ -> openWebsite(sites[position]) }
        findViewById<TextView>(R.id.addWebsiteButton).setOnClickListener { showAddWebsiteDialog() }
    }

    private fun loadCustomSites(): List<Website> {
        val json = preferences.getString("custom_sites", "[]") ?: "[]"
        return try {
            val array = JSONArray(json)
            (0 until array.length()).mapNotNull { index ->
                val item = array.optJSONObject(index) ?: return@mapNotNull null
                val name = item.optString("name").trim()
                val url = item.optString("url").trim()
                if (name.isNotEmpty() && url.isNotEmpty()) Website(name, url) else null
            }
        } catch (_: Exception) {
            emptyList()
        }
    }

    private fun saveCustomSites() {
        val array = JSONArray()
        sites.drop(5).forEach { site ->
            array.put(JSONObject().put("name", site.name).put("url", site.url))
        }
        preferences.edit().putString("custom_sites", array.toString()).apply()
    }

    private fun showAddWebsiteDialog() {
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            setPadding(24.dp(), 8.dp(), 24.dp(), 0)
        }
        val nameInput = EditText(this).apply {
            hint = "Website name"
            singleLine = true
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_FLAG_CAP_SENTENCES
        }
        val urlInput = EditText(this).apply {
            hint = "Website URL (example.com)"
            singleLine = true
            inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_URI
        }
        container.addView(nameInput)
        container.addView(urlInput)

        val dialog = AlertDialog.Builder(this)
            .setTitle("Add a website")
            .setView(container)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("Add", null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val name = nameInput.text.toString().trim()
                var url = urlInput.text.toString().trim()
                if (url.isNotEmpty() && !url.contains("://")) url = "https://$url"
                val uri = android.net.Uri.parse(url)
                if (name.isBlank() || (uri.scheme != "https" && uri.scheme != "http") || uri.host.isNullOrBlank()) {
                    Toast.makeText(this, "Enter a name and a valid website URL", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                sites.add(Website(name, uri.toString()))
                saveCustomSites()
                (findViewById<GridView>(R.id.websiteGrid).adapter as WebsiteAdapter).notifyDataSetChanged()
                dialog.dismiss()
            }
        }
        dialog.show()
    }

    private fun openWebsite(site: Website) {
        startActivity(Intent(this, WebViewActivity::class.java).apply {
            putExtra(WebViewActivity.EXTRA_NAME, site.name)
            putExtra(WebViewActivity.EXTRA_URL, site.url)
        })
    }

    private fun Int.dp(): Int = (this * resources.displayMetrics.density).toInt()

    private inner class WebsiteAdapter : BaseAdapter() {
        override fun getCount() = sites.size
        override fun getItem(position: Int) = sites[position]
        override fun getItemId(position: Int) = position.toLong()

        override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
            val view = convertView ?: LayoutInflater.from(this@MainActivity)
                .inflate(R.layout.item_website, parent, false)
            val site = getItem(position)
            view.findViewById<TextView>(R.id.siteName).text = site.name
            view.findViewById<TextView>(R.id.siteIcon).text = site.name.firstOrNull()?.uppercase() ?: "?"
            return view
        }
    }
}

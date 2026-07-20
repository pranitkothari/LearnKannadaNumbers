package com.learnkannadanumbers.app.data

/**
 * Everyday vocabulary grouped into categories. Every word/phrase here was
 * cross-checked against multiple independent web sources (not recalled from
 * memory alone), the same standard applied to the numbers data.
 */
object KannadaWords {

    enum class Category(val label: String) {
        COLORS("Colors"),
        DAYS("Days of the week"),
        FAMILY("Family"),
        GREETINGS("Greetings"),
        FOOD("Food"),
    }

    // Kannada, romanization, English meaning
    private val colors = listOf(
        Triple("ಕೆಂಪು", "kempu", "Red"),
        Triple("ಹಸಿರು", "hasiru", "Green"),
        Triple("ನೀಲಿ", "neeli", "Blue"),
        Triple("ಹಳದಿ", "haladi", "Yellow"),
        Triple("ಬಿಳಿ", "bili", "White"),
        Triple("ಕಪ್ಪು", "kappu", "Black"),
        Triple("ನೇರಳೆ", "nerale", "Purple"),
        Triple("ಕಿತ್ತಳೆ", "kittale", "Orange"),
        Triple("ಗುಲಾಬಿ", "gulaabi", "Pink"),
        Triple("ಬೂದು", "buudu", "Grey"),
    )

    private val days = listOf(
        Triple("ಭಾನುವಾರ", "bhaanuvaara", "Sunday"),
        Triple("ಸೋಮವಾರ", "somavaara", "Monday"),
        Triple("ಮಂಗಳವಾರ", "mangalavaara", "Tuesday"),
        Triple("ಬುಧವಾರ", "budhavaara", "Wednesday"),
        Triple("ಗುರುವಾರ", "guruvaara", "Thursday"),
        Triple("ಶುಕ್ರವಾರ", "shukravaara", "Friday"),
        Triple("ಶನಿವಾರ", "shanivaara", "Saturday"),
    )

    private val family = listOf(
        Triple("ಅಪ್ಪ", "appa", "Father"),
        Triple("ಅಮ್ಮ", "amma", "Mother"),
        Triple("ಅಣ್ಣ", "anna", "Elder brother"),
        Triple("ಅಕ್ಕ", "akka", "Elder sister"),
        Triple("ತಮ್ಮ", "thamma", "Younger brother"),
        Triple("ತಂಗಿ", "thangi", "Younger sister"),
        Triple("ಅಜ್ಜ", "ajja", "Grandfather"),
        Triple("ಅಜ್ಜಿ", "ajji", "Grandmother"),
        Triple("ಮಗ", "maga", "Son"),
        Triple("ಮಗಳು", "magalu", "Daughter"),
        Triple("ಗಂಡ", "ganda", "Husband"),
        Triple("ಹೆಂಡತಿ", "hendati", "Wife"),
    )

    private val greetings = listOf(
        Triple("ನಮಸ್ಕಾರ", "namaskaara", "Hello / Greetings"),
        Triple("ಹೇಗಿದ್ದೀರಿ", "hegiddiri", "How are you"),
        Triple("ಚೆನ್ನಾಗಿದ್ದೇನೆ", "chennaagiddeene", "I am fine"),
        Triple("ಧನ್ಯವಾದ", "dhanyavaada", "Thank you"),
        Triple("ಕ್ಷಮಿಸಿ", "kshamisi", "Sorry / Excuse me"),
        Triple("ದಯವಿಟ್ಟು", "dayavittu", "Please"),
        Triple("ಶುಭೋದಯ", "shubhodaya", "Good morning"),
        Triple("ಶುಭ ರಾತ್ರಿ", "shubha raatri", "Good night"),
        Triple("ಹೌದು", "haudu", "Yes"),
        Triple("ಇಲ್ಲ", "illa", "No"),
    )

    private val food = listOf(
        Triple("ಅಕ್ಕಿ", "akki", "Rice"),
        Triple("ರೊಟ್ಟಿ", "rotti", "Bread / roti"),
        Triple("ಹಾಲು", "haalu", "Milk"),
        Triple("ನೀರು", "neeru", "Water"),
        Triple("ಸಕ್ಕರೆ", "sakkare", "Sugar"),
        Triple("ಉಪ್ಪು", "uppu", "Salt"),
        Triple("ಹಣ್ಣು", "hannu", "Fruit"),
        Triple("ಬೆಲ್ಲ", "bella", "Jaggery"),
        Triple("ತರಕಾರಿ", "tarakaari", "Vegetable"),
        Triple("ಚಹಾ", "chahaa", "Tea"),
        Triple("ಮೊಟ್ಟೆ", "motte", "Egg"),
        Triple("ಬಾಳೆಹಣ್ಣು", "baalehannu", "Banana"),
    )

    private fun entriesFor(category: Category): List<Triple<String, String, String>> = when (category) {
        Category.COLORS -> colors
        Category.DAYS -> days
        Category.FAMILY -> family
        Category.GREETINGS -> greetings
        Category.FOOD -> food
    }

    fun itemsFor(category: Category): List<PracticeItem> =
        entriesFor(category).mapIndexed { index, (kannada, roman, meaning) ->
            PracticeItem(
                id = "word:${category.name}:$index",
                kannada = kannada,
                transliteration = roman,
                displayLabel = meaning,
            )
        }

    fun allItems(): List<PracticeItem> = Category.entries.flatMap { itemsFor(it) }
}

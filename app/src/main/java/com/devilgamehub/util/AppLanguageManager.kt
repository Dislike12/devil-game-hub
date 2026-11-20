package com.devilgamehub.util

object AppLanguageManager {

    fun getString(key: String, language: String): String {
        val langKey = language.lowercase()
        val dict = translations[key] ?: return key

        return when {
            langKey.contains("español") || langKey.contains("spanish") -> dict["es"] ?: dict["en"] ?: key
            langKey.contains("français") || langKey.contains("french") -> dict["fr"] ?: dict["en"] ?: key
            langKey.contains("deutsch") || langKey.contains("german") -> dict["de"] ?: dict["en"] ?: key
            langKey.contains("日本語") || langKey.contains("japanese") -> dict["ja"] ?: dict["en"] ?: key
            langKey.contains("português") || langKey.contains("portuguese") -> dict["pt"] ?: dict["en"] ?: key
            langKey.contains("हिन्दी") || langKey.contains("hindi") -> dict["hi"] ?: dict["en"] ?: key
            else -> dict["en"] ?: key
        }
    }

    private val translations = mapOf(
        "home" to mapOf(
            "en" to "Home", "es" to "Inicio", "fr" to "Accueil", "de" to "Start", "ja" to "ホーム", "pt" to "Início", "hi" to "होम"
        ),
        "library" to mapOf(
            "en" to "Library", "es" to "Biblioteca", "fr" to "Bibliothèque", "de" to "Bibliothek", "ja" to "ライブラリ", "pt" to "Biblioteca", "hi" to "लाइब्रेरी"
        ),
        "favorites" to mapOf(
            "en" to "Favorites", "es" to "Favoritos", "fr" to "Favoris", "de" to "Favoriten", "ja" to "お気に入り", "pt" to "Favoritos", "hi" to "पसंदीदा"
        ),
        "settings" to mapOf(
            "en" to "Settings", "es" to "Ajustes", "fr" to "Paramètres", "de" to "Einstellungen", "ja" to "設定", "pt" to "Configurações", "hi" to "सेटिंग्स"
        ),
        "featured" to mapOf(
            "en" to "FEATURED", "es" to "DESTACADOS", "fr" to "EN VEDETTE", "de" to "EMPFOHLEN", "ja" to "注目", "pt" to "EM DESTAQUE", "hi" to "विशेष"
        ),
        "popular_games" to mapOf(
            "en" to "POPULAR GAMES", "es" to "JUEGOS POPULARES", "fr" to "JEUX POPULAIRES", "de" to "BELIEBTE SPIELE", "ja" to "人気ゲーム", "pt" to "JOGOS POPULARES", "hi" to "लोकप्रिय खेल"
        ),
        "categories" to mapOf(
            "en" to "CATEGORIES", "es" to "CATEGORÍAS", "fr" to "CATÉGORIES", "de" to "KATEGORIEN", "ja" to "カテゴリ", "pt" to "CATEGORIAS", "hi" to "श्रेणियां"
        ),
        "recently_played" to mapOf(
            "en" to "RECENTLY PLAYED", "es" to "JUGADOS RECIENTEMENTE", "fr" to "JOUÉS RÉCEMMENT", "de" to "ZULETZT GESPIELT", "ja" to "最近プレイした", "pt" to "JOGADOS RECENTEMENTE", "hi" to "हाल ही में खेले गए"
        ),
        "play_now" to mapOf(
            "en" to "PLAY NOW", "es" to "JUGAR AHORA", "fr" to "JOUER MAINTENANT", "de" to "JETZT SPIELEN", "ja" to "今すぐプレイ", "pt" to "JOGAR AGORA", "hi" to "अभी खेलें"
        ),
        "launch_game" to mapOf(
            "en" to "LAUNCH GAME", "es" to "INICIAR JUEGO", "fr" to "LANCER LE JEU", "de" to "SPIEL STARTEN", "ja" to "ゲーム開始", "pt" to "INICIAR JOGO", "hi" to "गेम शुरू करें"
        ),
        "search_placeholder" to mapOf(
            "en" to "Search games by title, category, tags...",
            "es" to "Buscar juegos por título, categoría, etiquetas...",
            "fr" to "Rechercher des jeux par titre, catégorie, tags...",
            "de" to "Spiele nach Titel, Kategorie, Tags suchen...",
            "ja" to "タイトル、カテゴリ、タグでゲームを検索...",
            "pt" to "Pesquisar jogos por título, categoria, tags...",
            "hi" to "शीर्षक, श्रेणी, टैग द्वारा गेम खोजें..."
        ),
        "settings_title" to mapOf(
            "en" to "SETTINGS & PREFERENCES", "es" to "AJUSTES Y PREFERENCIAS", "fr" to "PARAMÈTRES ET PRÉFÉRENCES", "de" to "EINSTELLUNGEN", "ja" to "設定と環境設定", "pt" to "CONFIGURAÇÕES E PREFERÊNCIAS", "hi" to "सेटिंग्स और प्राथमिकताएं"
        ),
        "sound_settings" to mapOf(
            "en" to "Sound & Audio Settings", "es" to "Ajustes de Sonido y Audio", "fr" to "Paramètres du Son", "de" to "Sound- & Audio-Einstellungen", "ja" to "サウンド＆オーディオ設定", "pt" to "Configurações de Som e Áudio", "hi" to "ध्वनि और ऑडियो सेटिंग्स"
        ),
        "language_settings" to mapOf(
            "en" to "Language Preferences", "es" to "Preferencias de Idioma", "fr" to "Préférences Linguistiques", "de" to "Spracheinstellungen", "ja" to "言語設定", "pt" to "Preferências de Idioma", "hi" to "भाषा प्राथमिकताएं"
        ),
        "gameplay_settings" to mapOf(
            "en" to "Gameplay Preferences", "es" to "Preferencias de Juego", "fr" to "Préférences de Jeu", "de" to "Gameplay-Einstellungen", "ja" to "ゲームプレイ設定", "pt" to "Preferências de Jogo", "hi" to "गेमप्ले प्राथमिकताएं"
        ),
        "theme_settings" to mapOf(
            "en" to "App Theme Settings", "es" to "Ajustes de Tema de App", "fr" to "Paramètres de Thème", "de" to "App-Design-Einstellungen", "ja" to "アプリのテーマ設定", "pt" to "Configurações de Tema do App", "hi" to "ऐप थीम सेटिंग्स"
        ),
        "clear_history" to mapOf(
            "en" to "Clear Play History", "es" to "Borrar Historial", "fr" to "Effacer l'historique", "de" to "Verlauf löschen", "ja" to "履歴を消去", "pt" to "Limpar Histórico", "hi" to "इतिहास साफ़ करें"
        )
    )
}

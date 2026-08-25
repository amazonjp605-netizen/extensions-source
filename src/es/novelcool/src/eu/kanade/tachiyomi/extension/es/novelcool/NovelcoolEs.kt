package eu.kanade.tachiyomi.extension.es.novelcool

import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.model.FilterList
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SChapter
import eu.kanade.tachiyomi.source.model.SManga
import eu.kanade.tachiyomi.source.online.ParsedHttpSource
import okhttp3.Request
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

class NovelcoolEs : ParsedHttpSource() {

    override val name = "Novelcool (ES)"
    override val baseUrl = "https://es.novelcool.com"
    override val lang = "es"
    override val supportsLatest = true

    // ============================== Catálogo ===============================
    override fun popularMangaRequest(page: Int): Request {
        return GET("$baseUrl/category.html?page=$page", headers)
    }

    override fun popularMangaSelector() = ".book-item, .manga-list-item" 

    override fun popularMangaFromElement(element: Element): SManga {
        val manga = SManga.create()
        manga.setUrlWithoutDomain(element.select("a").first()?.attr("href") ?: "")
        manga.title = element.select(".title, .book-name, h3").text()
        manga.thumbnail_url = element.select("img").attr("src")
        return manga
    }

    override fun popularMangaNextPageSelector() = ".next-page, .pagination .next" 

    // =============================== Recientes ===============================
    override fun latestUpdatesRequest(page: Int): Request {
        return GET("$baseUrl/latest.html?page=$page", headers) 
    }
    override fun latestUpdatesSelector() = popularMangaSelector()
    override fun latestUpdatesFromElement(element: Element): SManga = popularMangaFromElement(element)
    override fun latestUpdatesNextPageSelector() = popularMangaNextPageSelector()

    // =============================== Búsqueda ===============================
    override fun searchMangaRequest(page: Int, query: String, filters: FilterList): Request {
        return GET("$baseUrl/search.html?keyword=$query&page=$page", headers)
    }
    override fun searchMangaSelector() = popularMangaSelector()
    override fun searchMangaFromElement(element: Element): SManga = popularMangaFromElement(element)
    override fun searchMangaNextPageSelector() = popularMangaNextPageSelector()

    // =========================== Detalles del Manga ============================
    override fun mangaDetailsParse(document: Document): SManga {
        val manga = SManga.create()
        manga.author = document.select(".author, .bookinfo-author").text()
        manga.description = document.select(".description, .bookinfo-desc").text()
        manga.genre = document.select(".genre, .bookinfo-type").joinToString { it.text() }
        manga.thumbnail_url = document.select(".cover img, .bookinfo-pic img").attr("src")
        
        val statusText = document.select(".status, .bookinfo-status").text()
        manga.status = when {
            statusText.contains("Completado", ignoreCase = true) -> SManga.COMPLETED
            statusText.contains("Emisión", ignoreCase = true) -> SManga.ONGOING
            else -> SManga.UNKNOWN
        }
        return manga
    }

    // ============================== Capítulos ==============================
    override fun chapterListSelector() = ".chapter-item, .chapter-list li" 

    override fun chapterFromElement(element: Element): SChapter {
        val chapter = SChapter.create()
        chapter.setUrlWithoutDomain(element.select("a").attr("href"))
        chapter.name = element.select(".chapter-name, .title").text()
        chapter.date_upload = System.currentTimeMillis() 
        return chapter
    }

    // =============================== Lector de Páginas ================================
    override fun pageListParse(document: Document): List<Page> {
        return document.select(".reader-page-image, .chapter-img img, .mangaread-img img").mapIndexed { i, img ->
            val url = img.attr("data-src").takeIf { it.isNotEmpty() } ?: img.attr("src")
            Page(i, "", url)
        }
    }

    override fun imageUrlParse(document: Document): String = throw UnsupportedOperationException("Not used.")
}

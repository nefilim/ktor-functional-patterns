package io.github.nefilim.ktorpatterns.web.html

import kotlinx.html.HTML
import kotlinx.html.body
import kotlinx.html.h1
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.meta
import kotlinx.html.title

fun HTML.landing() {
    head {
        meta(charset = "utf-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1, shrink-to-fit=no")
        title { +"Ktor Patterns" }

        link(rel = "stylesheet", href = "https://unpkg.com/bootstrap-table@1.18.3/dist/bootstrap-table.min.css")
    }
    body {
        h1 {
            +"Hello World!"
        }
    }
}
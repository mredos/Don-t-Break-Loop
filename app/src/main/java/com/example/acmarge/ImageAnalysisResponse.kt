package com.example.acmarge

data class ImageAnalysisResponse(
    val categories: List<Category>? = null,
    val tags: List<Tag>?,
    val description: Description? = null,
    val requestId: String? = null,
    val metadata: Metadata? = null
)

data class Category(
    val name: String? = null,
    val score: Double? = null
)

data class Tag(
    val name: String,
    val confidence: Double? = null
)

data class Description(
    val tags: List<String>? = null,
    val captions: List<Caption>? = null
)

data class Caption(
    val text: String? = null,
    val confidence: Double? = null
)

data class Metadata(
    val width: Int? = null,
    val height: Int? = null,
    val format: String? = null
)

package com.pbec.preboardexamchecker.ui.scanner.processor

import com.pbec.preboardexamchecker.data.models.Student

/**
 * setAQuestionIds/setBQuestionIds are ordered to match the printed PDF: physical answer
 * position i maps to setXQuestionIds[i]. Roster rows remain a list so duplicate IDs
 * cannot silently overwrite each other before student matching.
 */
data class ScanContext(
    val examId: Long,
    val examName: String,
    val subject: String,
    val setAQuestionIds: List<Long>,
    val setBQuestionIds: List<Long>,
    val setAKeyMap: Map<Long, String?>,
    val setBKeyMap: Map<Long, String?>,
    val students: List<Student>,
    // Non-null when this context belongs to a cluster session; tags results for per-cluster GWA.
    val clusterId: Long? = null,
    val clusterName: String? = null,
)

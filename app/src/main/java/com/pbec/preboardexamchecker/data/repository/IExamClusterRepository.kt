package com.pbec.preboardexamchecker.data.repository

import com.pbec.preboardexamchecker.data.models.ExamCluster

/** Scanner-path access to clusters; the concrete repo additionally exposes a Flow for UI. */
interface IExamClusterRepository {
    suspend fun getClustersOnce(): List<ExamCluster>
    suspend fun getClusterById(clusterId: Long): ExamCluster?
    suspend fun getClustersForOfflinePreparation(): List<ExamCluster> =
        throw UnsupportedOperationException("Server preparation is unavailable")
}

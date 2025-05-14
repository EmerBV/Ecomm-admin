package com.emerbv.ecommadmin.features.dashboard.di

import com.emerbv.ecommadmin.features.products.domain.*
import com.emerbv.ecommadmin.features.dashboard.presentation.DashboardViewModel
import io.ktor.client.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import org.koin.core.module.dsl.factoryOf

import org.koin.dsl.module

val dashboardModule = module {
    // ViewModels
    factoryOf(::DashboardViewModel)
}
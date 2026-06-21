package com.iqbalfauzi.kitchenstockmobile.di

import com.iqbalfauzi.kitchenstockmobile.domain.usecase.GetHomeSummaryUseCase
import org.koin.core.module.dsl.factoryOf
import org.koin.dsl.module

val domainModule = module {
    factoryOf(::GetHomeSummaryUseCase)
}

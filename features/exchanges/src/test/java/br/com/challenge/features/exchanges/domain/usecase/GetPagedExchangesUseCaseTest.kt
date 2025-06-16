package br.com.challenge.features.exchanges.domain.usecase

import androidx.paging.PagingSource
import br.com.challenge.core.database.entity.ExchangeEntity
import br.com.challenge.features.exchanges.domain.repository.ExchangeRepository
import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class GetPagedExchangesUseCaseTest {

    private val repository = mockk<ExchangeRepository>()
    private val useCase = GetPagedExchangesUseCase(repository)
    private val pagingSource = mockk<PagingSource<Int, ExchangeEntity>>()

    @Test
    fun `invoke should return PagingSource from repository`() {
        every { repository.getPagedItems() } returns pagingSource

        val result = useCase.invoke()

        assertEquals(pagingSource, result)
    }
}
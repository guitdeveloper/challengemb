package br.com.challenge.features.exchanges.presentation.list

import androidx.recyclerview.widget.DiffUtil
import br.com.challenge.features.exchanges.domain.model.ExchangeResume

class ExchangeResumeDiffCallback : DiffUtil.ItemCallback<ExchangeResume>() {
    override fun areItemsTheSame(oldItem: ExchangeResume, newItem: ExchangeResume): Boolean {
        return oldItem.exchangeId == newItem.exchangeId
    }

    override fun areContentsTheSame(oldItem: ExchangeResume, newItem: ExchangeResume): Boolean {
        return oldItem == newItem
    }
}
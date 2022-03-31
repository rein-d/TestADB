package com.rein.android.ReynTestApp.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment

import com.rein.android.ReynTestApp.databinding.FragmentStartBinding
import ru.evotor.framework.core.IntegrationManagerCallback
import ru.evotor.framework.core.IntegrationManagerFuture
import ru.evotor.framework.core.action.command.print_z_report_command.PrintZReportCommand
import ru.evotor.framework.core.action.command.print_z_report_command.PrintZReportCommandResult
import ru.evotor.framework.kkt.FfdVersion
import ru.evotor.framework.kkt.api.KktApi
import ru.evotor.framework.receipt.Position
import java.util.*
import kotlin.collections.ArrayList

class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        Log.d("frgmnt", "Fragment started")
        binding = FragmentStartBinding.inflate(inflater, container, false)

        binding.correctionButton.setOnClickListener { correction() }
        binding.closeSessionButton.setOnClickListener { closeSession() }

        return binding.root
    }

    private fun correction() {
        when (KktApi.getRegisteredFfdVersion(requireContext())) {
            FfdVersion.V_1_2_0 -> {
                val correctableSettlementDate = Calendar.getInstance().time
                val positions = ArrayList<Position>()
            }
        }
    }

    private fun closeSession() {
        val execute = PrintZReportCommand().process(requireContext(), IntegrationManagerCallback {
            when (it.result.type) {
                IntegrationManagerFuture.Result.Type.OK -> PrintZReportCommandResult.create(it.result.data)
                IntegrationManagerFuture.Result.Type.ERROR ->
                    Toast.makeText(requireContext(), it.result.error.message, Toast.LENGTH_LONG).show()
                null -> throw RuntimeException("No results")
            }
        })
        return execute
    }

    companion object {
        @JvmStatic
        fun newInstance(): StartFragment {
            return StartFragment()
        }
    }
}
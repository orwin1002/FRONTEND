package com.example.evmate

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.evmate.databinding.FragmentStationListBinding
import com.example.evmate.model.StationRepo

class StationListFragment : Fragment() {
    private var _binding: FragmentStationListBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: StationListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentStationListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = StationListAdapter(StationRepo.stations) { station ->
            val intent = Intent(requireContext(), StationDetailActivity::class.java)
            intent.putExtra("station", station)
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
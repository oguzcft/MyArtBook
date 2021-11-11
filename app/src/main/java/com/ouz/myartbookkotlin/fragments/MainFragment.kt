package com.ouz.myartbookkotlin.fragments

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager

import com.ouz.myartbookkotlin.Adapter.Adapter
import com.ouz.myartbookkotlin.R
import com.ouz.myartbookkotlin.data.ArtDataBase
import com.ouz.myartbookkotlin.data.ImagesEntity

import com.ouz.myartbookkotlin.databinding.FragmentMainBinding


class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private lateinit var imageDB: ArtDataBase
    private lateinit var imageList: List<ImagesEntity?>




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageDB = ArtDataBase.getImagesDataBase(requireContext())!!
        imageList=imageDB.artDao().allImages()

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMainBinding.inflate(inflater, container, false)

        setHasOptionsMenu(true)
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val rvAdapter = Adapter(imageList)
        binding.apply {
            if (imageList.isNotEmpty()){
                recyclerViewFragment.adapter=rvAdapter
                recyclerViewFragment.layoutManager=LinearLayoutManager(context)
                recyclerViewFragment.setHasFixedSize(true)

            }else{
                Toast.makeText(context,"Empty List",Toast.LENGTH_LONG).show()
            }
        }
        rvAdapter.userInfoTransfer=::imagesToInfo
    }
  private  fun imagesToInfo(select:ImagesEntity){
      findNavController().navigate(MainFragmentDirections.mainToInfo(select.id))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.addButtonMenu -> findNavController().navigate(MainFragmentDirections.mainToAdd())
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
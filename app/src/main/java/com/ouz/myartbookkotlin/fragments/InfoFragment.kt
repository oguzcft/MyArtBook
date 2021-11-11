package com.ouz.myartbookkotlin.fragments

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.ouz.myartbookkotlin.R
import com.ouz.myartbookkotlin.data.ArtDataBase
import com.ouz.myartbookkotlin.data.ImagesEntity
import com.ouz.myartbookkotlin.databinding.FragmentAddBinding
import com.ouz.myartbookkotlin.databinding.FragmentInfoBinding


class InfoFragment : Fragment() {
    private val args: InfoFragmentArgs by navArgs()

    private var _binding: FragmentInfoBinding? = null
    private val binding get() = _binding!!

    private lateinit var artDataBase: ArtDataBase
    private lateinit var imageInfo: ImagesEntity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        artDataBase = ArtDataBase.getImagesDataBase(requireContext())!!
        imageInfo = artDataBase.artDao().getUser(args.id)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentInfoBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.apply {
            imageViewInfo.setImageBitmap(Converters().toBitmap(imageInfo.image))
            textView.text = "Art name: " + imageInfo.artName
            textView2.text = "Artist name: " + imageInfo.artistName
            textView3.text = "Year : " + imageInfo.year.toString()

            deleteButton.setOnClickListener {
                Snackbar.make(
                    it,
                    "Are you sure?",
                    Snackbar.LENGTH_INDEFINITE
                ).setAction("Yes", View.OnClickListener {

                   val deleteImage=ImagesEntity(artName = imageInfo.artName,artistName = imageInfo.artistName,year = imageInfo.year,image = imageInfo.image)
                    deleteImage.id=args.id
                    artDataBase.artDao().deleteImages(deleteImage)


                    findNavController().navigate(InfoFragmentDirections.infoToMain())
                }).show()

            }
            updateButton.setOnClickListener {
                findNavController().navigate(InfoFragmentDirections.infoToAdd(args.id, "update"))
            }
        }

    }


    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}
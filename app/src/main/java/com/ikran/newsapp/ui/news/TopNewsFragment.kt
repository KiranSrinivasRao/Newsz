package com.ikran.newsapp.ui.news

import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.ikran.newsapp.R
import com.ikran.newsapp.util.Resource

class TopNewsFragment : Fragment() {

    companion object {
        fun newInstance() = TopNewsFragment()
        val logTag:String = TopNewsFragment::class.java.simpleName
    }

    lateinit var viewModel: NewsViewModel
    private lateinit var recyclerView: RecyclerView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.search_bookbark_menu, menu)
        val searchItem:MenuItem = menu.findItem(R.id.menu_search_item)
        val searchView = searchItem.actionView as SearchView

        searchView.apply {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener{
                override fun onQueryTextSubmit(query: String?): Boolean {
                    Log.i(logTag, "onQueryTextSubmit: $query")
                    //hideKeyboard() & hit api
                    return true
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    Log.i(logTag, "onQueryTextChange: $newText")
                    return true
                }

            })
            // Todo
            setOnSearchClickListener {
                searchView.setQuery("TODO", false)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view = inflater.inflate(R.layout.fragment_top_news, container, false)
        recyclerView = view.findViewById(R.id.recyclerViewPopularNews)

        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        runCatching {

        viewModel = (activity as NewsActivity).viewModel



        viewModel.topNews.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is Resource.Success -> {
                    hideProgressBar()
                    response.data?.let { newsResponse ->
                       // newsAdapter.differ.submitList(newsResponse.articles)
                    Log.i(logTag, newsResponse.toString())
                    }
                }

                is Resource.Error -> {
                    hideProgressBar()
                    try{
                    response.message?.let { message ->
                        Log.e(logTag, message)
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                    }catch (e: Exception){
                        System.out.println("Error Fragment --- "+ e.message)
                    }
                }

                is Resource.Loading -> { showProgressBar()}
                //else -> {}
            }

        })

        }

    }

    private fun showProgressBar() {
        Log.e(logTag, "showProgressBar")
    }

    private fun hideProgressBar() {
        Log.e(logTag, "hideProgressBar")
    }

 /*   override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        //  Use the ViewModel
    }*/

}
package com.ikran.newsapp.ui.news

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.ikran.newsapp.repository.NewsRepository
import com.ikran.newsapp.NewsApp
import com.ikran.newsapp.data.NewsApiResponse
import com.ikran.newsapp.util.Resource
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(app : Application, val newsRepository: NewsRepository)
    : AndroidViewModel(app) {

    val topNews: MutableLiveData<Resource<NewsApiResponse>> = MutableLiveData()
    var topNewsPage  = 1

    val searchNews: MutableLiveData<Resource<NewsApiResponse>> = MutableLiveData()
    var searchNewsPage  = 1

    init {
        getTopNews(countryCode = "in")

        searchNews("tesla")
    }

    fun getTopNews(countryCode:String, pageNumber:Int =1) = viewModelScope.launch{
        safeTopNewsCall(countryCode, pageNumber)
    }

    fun searchNews(query:String, pageNumber:Int =10) = viewModelScope.launch {
        safeSearchNewsCall(query, pageNumber)
    }


    private fun handleTopNewsResponse(response: Response<NewsApiResponse>):Resource<NewsApiResponse>{
        if(response.isSuccessful){
            response.body()?.let {
                    resultResponse ->
                return  Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())

    }

    private fun handleSearchNewsResponse(response: Response<NewsApiResponse>):Resource<NewsApiResponse>{
        if(response.isSuccessful){
            response.body()?.let {
                    resultResponse ->
                return  Resource.Success(resultResponse)
            }
        }
        return Resource.Error(response.message())

    }

    private fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<NewsApp>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)?: return false

        return when{
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> { false}
        }
    }

    private suspend fun safeTopNewsCall(countryCode: String, pageNumber: Int){
        topNews.postValue(Resource.Loading())
        try {
            if(hasInternetConnection()) {
                val response = newsRepository.getTopNews(countryCode, pageNumber)
                topNews.postValue(handleTopNewsResponse(response))
            }else{
                topNews.postValue(Resource.Error("No Internet Connection - Actual"))
            }
        }catch (t: Throwable){
            when(t){
                is IOException -> topNews.postValue(Resource.Error("Network Failure"))
                else -> topNews.postValue(Resource.Error("Conversion Error"))
            }
        }

    }

    private suspend fun safeSearchNewsCall(query: String, pageNumber: Int) {
       searchNews.postValue(Resource.Loading())
        try{
        if(hasInternetConnection()){
            val response = newsRepository.searchNews(query, pageNumber)
            searchNews.postValue(handleSearchNewsResponse(response))
        }else{
            searchNews.postValue(Resource.Error("No Internet Connection - Actual"))
        }
        }catch (t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Error"))
            }
        }

    }

}
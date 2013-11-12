package com.github.techisfun.android.rssreader.robospice;

import android.app.Application;
import android.util.Log;

import com.github.techisfun.android.rssreader.model.RssArrayList;
import com.google.gson.Gson;
import com.octo.android.robospice.SpringAndroidSpiceService;
import com.octo.android.robospice.persistence.CacheManager;
import com.octo.android.robospice.persistence.binary.InFileBitmapObjectPersister;
import com.octo.android.robospice.persistence.exception.CacheCreationException;
import com.octo.android.robospice.persistence.exception.CacheLoadingException;
import com.octo.android.robospice.persistence.exception.CacheSavingException;
import com.octo.android.robospice.persistence.file.InFileObjectPersister;
import com.octo.android.robospice.persistence.string.InFileStringObjectPersister;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.CharEncoding;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Created by admin on 04/10/13.
 */
public class RssSpiceService extends SpringAndroidSpiceService {

    @Override
    public CacheManager createCacheManager(Application application) throws CacheCreationException {
        CacheManager cacheManager = new CacheManager();

        cacheManager.addPersister(new InFileBitmapObjectPersister(application));
        cacheManager.addPersister(new InFileStringObjectPersister(application));
        cacheManager.addPersister(new RssArrayListPersister(application));

        /*
        InFileStringObjectPersister inFileStringObjectPersister = new InFileStringObjectPersister(
                application);
        InFileInputStreamObjectPersister inFileInputStreamObjectPersister = new InFileInputStreamObjectPersister(
                application);

        inFileStringObjectPersister.setAsyncSaveEnabled(true);
        inFileInputStreamObjectPersister.setAsyncSaveEnabled(true);

        cacheManager.addPersister(inFileStringObjectPersister);
        cacheManager.addPersister(inFileInputStreamObjectPersister);
        */

        return cacheManager;
    }

    @Override
    public RestTemplate createRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        HttpComponentsClientHttpRequestFactory httpRequestFactory = new HttpComponentsClientHttpRequestFactory();
        httpRequestFactory.setReadTimeout(10000);
        httpRequestFactory.setConnectTimeout(10000);
        restTemplate.setRequestFactory(httpRequestFactory);

        StringHttpMessageConverter stringHttpMessageConverter = new StringHttpMessageConverter();
        FormHttpMessageConverter formHttpMessageConverter = new FormHttpMessageConverter();
        final List<HttpMessageConverter<?>> listHttpMessageConverters = restTemplate.getMessageConverters();

        listHttpMessageConverters.add(formHttpMessageConverter);
        listHttpMessageConverters.add(stringHttpMessageConverter);
        restTemplate.setMessageConverters(listHttpMessageConverters);

        return restTemplate;
    }

    class RssArrayListPersister extends InFileObjectPersister<RssArrayList> {

        public RssArrayListPersister(Application application) throws CacheCreationException {
            super(application, RssArrayList.class);
        }

        @Override
        public boolean canHandleClass(Class<?> clazz) {
            return clazz.equals(RssArrayList.class);
        }

        @Override
        protected RssArrayList readCacheDataFromFile(File file) throws CacheLoadingException {
            try {
                String json = FileUtils.readFileToString(file, CharEncoding.UTF_8);
                return new Gson().fromJson(json, RssArrayList.class);
            } catch (FileNotFoundException e) {
                Log.w(getClass().getSimpleName(), "file " + file.getAbsolutePath() + " does not exists", e);
                return null;
            } catch (Exception e) {
                throw new CacheLoadingException(e);
            }
        }

        @Override
        public RssArrayList saveDataToCacheAndReturnData(final RssArrayList data, final Object cacheKey) throws CacheSavingException {
            Log.d(getClass().getSimpleName(), "Saving String " + data + " into cacheKey = " + cacheKey);

            final String gson = new Gson().toJson(data);

            try {
                if (isAsyncSaveEnabled()) {
                    Thread t = new Thread() {
                        @Override
                        public void run() {
                            try {
                                FileUtils.writeStringToFile(getCacheFile(cacheKey), gson, CharEncoding.UTF_8);
                            } catch (IOException e) {
                                Log.w(getClass().getSimpleName(), "An error occured on saving request " + cacheKey + " data asynchronously", e);
                            }
                        }

                        ;
                    };
                    t.start();
                } else {
                    FileUtils.writeStringToFile(getCacheFile(cacheKey), gson, CharEncoding.UTF_8);
                }
            } catch (Exception e) {
                throw new CacheSavingException(e);
            }
            return data;
        }
    }

}

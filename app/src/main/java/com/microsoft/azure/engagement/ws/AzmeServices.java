// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.ws;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import javax.xml.parsers.ParserConfigurationException;

import com.microsoft.azure.engagement.ws.FeedParser.FeedItem;
import com.microsoft.azure.engagement.ws.VideoParser.VideoItem;
import org.json.JSONException;
import org.xml.sax.SAXException;

public final class AzmeServices
{

  private static volatile AzmeServices instance;

  public static final class AsyncTaskResult<T>
  {

    private T result;

    private Exception error;

    public T getResult()
    {
      return result;
    }

    public Exception getError()
    {
      return error;
    }

    public AsyncTaskResult(T result)
    {
      super();
      this.result = result;
    }

    public AsyncTaskResult(Exception error)
    {
      super();
      this.error = error;
    }

  }

  /**
   * Returns the AzmeServices object associated with the current class.
   *
   * @return The instance of AzmeService
   */
  public static final AzmeServices getInstance()
  {
    if (instance == null)
    {
      synchronized (AzmeServices.class)
      {
        if (instance == null)
        {
          instance = new AzmeServices();
        }
      }
    }
    return instance;
  }

  /**
   * Method that parses the last update
   *
   * @return The list of feeds
   * @throws IOException, ParserConfigurationException, SAXException
   */
  public final List<FeedItem> getLastUpdate(String url)
      throws IOException, ParserConfigurationException, SAXException
  {
    final HttpURLConnection urlConnection = (HttpURLConnection) new URL(url).openConnection();

    try
    {
      urlConnection.connect();
      final InputStream inputStream = urlConnection.getInputStream();
      return FeedParser.parse(inputStream);
    }
    finally
    {
      if (urlConnection != null)
      {
        urlConnection.disconnect();
      }
    }
  }

  /**
   * Method that parses the videos
   *
   * @param urlString The urlString
   * @return The list of videos
   * @throws IOException, JSONException
   */
  public final List<VideoItem> getVideos(String urlString)
      throws IOException, JSONException
  {
    return VideoParser.parse(urlString);
  }

}

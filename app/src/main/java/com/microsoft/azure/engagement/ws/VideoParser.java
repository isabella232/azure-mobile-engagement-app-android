// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.ws;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public final class VideoParser
{

  private static final String TAG = VideoParser.class.getSimpleName();

  public static final class VideoItem
      implements Serializable
  {

    public final String title;

    public final String description;

    public final String pictureUrl;

    public final String videoUrl;

    public VideoItem(String title, String description, String pictureUrl, String videoUrl)
    {
      this.title = title;
      this.description = description;
      this.pictureUrl = pictureUrl;
      this.videoUrl = videoUrl;
    }

    /**
     * Method that tests if is an external video or a local one
     *
     * @return The video url nature
     */
    public final boolean isExternalVideo()
    {
      return videoUrl.startsWith("http://") || videoUrl.startsWith("https://");
    }

    /**
     * Method that tests if is an external image or a local one
     *
     * @return The image url nature
     */
    public final boolean isExternalImage()
    {
      return pictureUrl.startsWith("http://") || pictureUrl.startsWith("https://");
    }
  }

  /**
   * Method that parses the list of videos
   *
   * @return The list of videos
   * @throws IOException, JSONException
   */
  public static final List<VideoItem> parse(String urlString)
      throws IOException, JSONException
  {
    final ArrayList<VideoItem> videoItems = new ArrayList<>();

    final JSONObject object = VideoParser.getJsonObject(urlString);
    if ((object != null) && (object.has("result") == true))
    {
      final JSONArray items = object.getJSONArray("result");
      for (int index = 0; index < items.length(); index++)
      {
        final JSONObject item = items.getJSONObject(index);
        final VideoItem videoItem = new VideoItem(item.getString("title"), item.getString("description"), item.getString("pictureUrl"), item.getString("videoUrl"));
        videoItems.add(videoItem);
      }
    }

    videoItems.add(0, getTheFirstLocalVideoItem());

    return videoItems;
  }


  /**
   * Method that returns the result object
   *
   * @param urlString The url to parse
   * @return The result object
   */
  private static final JSONObject getJsonObject(String urlString)
  {
    Log.d(VideoParser.TAG, "Parse URL: " + urlString);

    InputStream inputStream = null;

    try
    {
      final URL url = new URL(urlString);
      final URLConnection urlConnection = url.openConnection();

      inputStream = new BufferedInputStream(urlConnection.getInputStream());
      final BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "utf-8"), 8);
      final StringBuilder sb = new StringBuilder();

      String line;
      while ((line = reader.readLine()) != null)
      {
        sb.append(line);
      }
      final String json = sb.toString();
      return new JSONObject(json);
    }
    catch (Exception e)
    {
      Log.w(VideoParser.TAG, "Failed to parse the json for media list", e);
      return null;
    }
    finally
    {
      if (inputStream != null)
      {
        try
        {
          inputStream.close();
        }
        catch (IOException e)
        {
          Log.w(VideoParser.TAG, "JSON feed closed", e);
        }
      }
    }

  }


  /**
   * Method that creates and returns the first video item
   *
   * @return The first video item
   */
  private static final VideoItem getTheFirstLocalVideoItem()
  {
    return new VideoItem("Azure Mobile Engagement Overview", "Brief overview of Azure Mobile Engagement service and the benefits it provides to the app publishers/marketers.", "mobileengagementoverview_960", "mobileengagementoverview_mid");
  }

}

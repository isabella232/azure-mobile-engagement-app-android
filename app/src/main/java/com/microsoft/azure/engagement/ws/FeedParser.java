// Copyright (c) Microsoft. All rights reserved.
// Licensed under the MIT license. See LICENSE file in the project root for full license information.

package com.microsoft.azure.engagement.ws;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public final class FeedParser
{

  public static final class FeedItem
  {

    public final String title;

    public final String publicationDate;

    public final String description;

    public final String link;

    public FeedItem(String title, String publicationDate, String description, String link)
    {
      this.title = title;
      this.publicationDate = publicationDate;
      this.description = cleanDescription(description);
      this.link = link;
    }

    /**
     * Method that cleans the feed's description
     *
     * @param description The description to clean
     * @return The description
     */
    private final String cleanDescription(String description)
    {
      final String firstPass = description.replaceAll("&lt;", "<");
      return firstPass.replaceAll("&gt;", ">");
    }

  }

  /**
   * Method that parses the list of feeds
   *
   * @param inputStream The inputStream to parse
   * @return The list of feeds
   * @throws ParserConfigurationException, IOException, SAXException
   */
  public static final List<FeedItem> parse(InputStream inputStream)
      throws ParserConfigurationException, IOException, SAXException
  {
    final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    final DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

    final Document document = documentBuilder.parse(inputStream);
    final Element rss = document.getDocumentElement();
    final Element firstChannel = (Element) rss.getElementsByTagName("channel").item(0);

    final ArrayList<FeedItem> feedItems = new ArrayList<>();

    final NodeList items = firstChannel.getElementsByTagName("item");
    for (int index = 0; index < items.getLength(); index++)
    {
      if (items.item(index).getNodeType() == Node.ELEMENT_NODE)
      {
        final Element item = (Element) items.item(index);
        final String title = item.getElementsByTagName("title").item(0).getTextContent();
        final String description = item.getElementsByTagName("description").item(0).getTextContent();
        final String publicationDate = item.getElementsByTagName("pubDate").item(0).getTextContent();
        final String link = item.getElementsByTagName("link").item(0).getTextContent();
        feedItems.add(new FeedItem(title, publicationDate, description, link));
      }
    }
    return feedItems;
  }

}

/* Copyright (c) 2009 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.opensocial.client;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * An object which represents a simple URL. Once an object is instantiated,
 * path components and parameters can be added. Once all of the elements
 * are in place, the object can be serialized into a string. This class
 * is used by internal classes and not by clients directly.
 *
 * @author apijason@google.com (Jason Cooper)
 */
class OpenSocialUrl {

  private String base;
  private List<String> components;
  private Map<String, String> queryStringParameters;
  private Map<String, String> urlTemplates;
  private Map<String, String> postAliases;

  public OpenSocialUrl(String base) {
    this.base = base;
    _initUrl();
  }

  public OpenSocialUrl(String base, String method) {
    this.base = base;
    _initUrl();
    addPathComponent(method);
  }

	private void _initUrl() {
	  components = new Vector<String>();
		queryStringParameters = new HashMap<String, String>();
		postAliases = new HashMap<String, String>();
		
		// URL Templates
		urlTemplates = new HashMap<String, String>();
		urlTemplates.put("people", "people/{userId}/{groupId}/{personId}");
		urlTemplates.put("activities", 
		    "activities/{userId}/{groupId}/{appId}/{activityId}");
		urlTemplates.put("appdata", "appdata/{userId}/{groupId}/{appId}");
		urlTemplates.put("messages", "messages/{userId}/outbox/{msgId}");
		urlTemplates.put("albums", "albums/{userId}/{groupId}/{albumId}");
		urlTemplates.put("mediaItems", 
		    "mediaItems/{userId}/{groupId}/{albumId}/{mediaItemId}");
		urlTemplates.put("statusmood", 
		    "statusmood/{userId}/{groupId}/{friendId}/{moodId}/{history}");
		urlTemplates.put("notifications", "notifications/{userId}/{groupId}");
		urlTemplates.put("groups", "/groups/{userId}/{groupId}");
		urlTemplates.put("profilecomments", "profilecomments/{userId}/{groupId}");
		
		// Pos Aliases
		postAliases.put("activities", "activity");
		postAliases.put("albums", "album");
		postAliases.put("appdata", "appdata");
		postAliases.put("mediaItems", "mediaItem");
		postAliases.put("messages", "message");
		postAliases.put("notifications", "notification");
		postAliases.put("people", "person");
		postAliases.put("statusmood", "statusMood");
		postAliases.put("profilecomments", "profileComment");
	}

  public Map<String, String> getPostAliases() {
	  return this.postAliases;
  }
  
  public String getUrlTemplate(String service) {
	  return this.urlTemplates.get(service);
  }
  
  /**
   * Adds passed String to the path component queue.
   * 
   * @param  component Path component to add
   */
  public void addPathComponent(String component) {
    components.add(component);
  }

  /**
   * Creates a new entry in queryStringParameters Map with the passed key and
   * value; used for adding URL parameters such as oauth_signature and the
   * various other OAuth parameters that are required in order to submit a
   * signed request.
   * 
   * @param  key Parameter name
   * @param  value Parameter value
   */
  public void addQueryStringParameter(String key, String value) {
    try {
      queryStringParameters.put(URLEncoder.encode(key, "UTF-8"),
          URLEncoder.encode(value, "UTF-8"));
    } catch (UnsupportedEncodingException e) {
      // "UTF-8" is a supported encoding so this exception should never be
      // thrown
    }
  }

  /**
   * Returns a String representing the serialized URL including the base
   * followed by any path components added to the path component queue
   * and, last but not least, appending any query string parameters as
   * name-value pairs after the full path.
   * 
   * @return
   */
  public String toString() {
    StringBuilder s = new StringBuilder(this.base);

    for (String pathComponent : this.components) {
      if (s.charAt(s.length() - 1) != '/') {
        s.append("/");
      }
      s.append(pathComponent);
    }

    char connector = '?';
    for (Map.Entry<String, String> e : this.queryStringParameters.entrySet()) {
      s.append(connector);
      s.append(e.getKey());
      s.append('=');
      s.append(e.getValue());
      connector = '&';
    }

    return s.toString();
  }

  public URL toURL() throws MalformedURLException {
    return new URL(this.toString());
  }
}
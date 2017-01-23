//
//
//
// DIT SCRIPT LOOPT VAST ONDER FF3 (v.3.0.10) op regel 
// gebruik voortaan cookie.js
//
//
//
//



//
//  Cookie Functions -- "A Nightmare on Cookie Street" (26-May-2002)
//
//  Based on "Night of the Living Cookie" (25-Jul-96)
//
//  Original work by:   Bill Dortch, hIdaho Design <bdortch@hidaho.com>
//  Array functions by: Sönke Tesch, <http://kino-fahrplan.de/intern/kontakt>
//
//  URL of this version: http://kino-fahrplan.de/privat/st/cookies/
//
//  The following functions are released to the public domain.
//
//
//  About Cookies
//  -------------
//  Introduction:
//  Cookies are little data packages that can be stored within the
//  user's browser on request of the server (either directly through
//  the HTTP protocol or by using Javascript).
//  They are perfect if you'd like to store e.g. user-specific settings
//  for some message board or create an online shop since you do not
//  need to run your own database. All data is stored on the user's
//  machine and delivered automatically back to your server if a page
//  is requested.
//
//  Usage hints, for pro's, too:
//  Unfortunatly the use of cookies is a pain in the ass today. Most
//  users love to have their settings appear again and gladly accept
//  a cookie for this, but they hate to be flooded with cookies without
//  a warning and, more important, without a real use for them.
//  Personally I wouldn't call personalized banner ads very useful,
//  especially if the company that serves the ads keeps track of all
//  of my steps. Would you like to have someone watching over your
//  shoulder while you read some newspaper or a magazine?
//
//  It should also be noted that the use of cookies _without warning_
//  is sort of illegal in some countries (e.g. Germany). You have to
//  ask before you set a cookie, at least in the case of cookies that
//  get not deleted when the current browser session ends.
//
//  So, what's a friendly cookie?
//
//  - Before you set a cookie, inform the user why you need to do so
//    and what's stored in there.
//
//  - Set only one cookie per page. Probably the most annoying habit
//    of some pages is to set cookies half a dozen times. This script
//    offers functions to store several values at once (called "array
//    cookies"), use them.
//    But also note that all Set.. functions store a cookie immediatly,
//    so use them with care, or to be exact: only one Set.. per script.
//
//  - Make your site usable without cookies, too.
//
//
//  Array Cookies
//  -------------
//  This version adds functions to store several values in one single
//  cookie, avoiding the need to possibly set a dozen cookies if you
//  want to store e.g. user preferences.
//  A cookie can store at least 4 Kbytes, some browsers allow you to
//  store even more - plenty of space, wasted if a cookie just holds
//  a handful of bytes. So why not put everything in one cookie?
//
//  The trick is to store all values in an array:
//
//    var acookie=new Array();
//    acookie["one"]=1;
//    acookie["two"]=2;
//    acookie["three"]=3;
//
//  This array is then fed to SetACookie(), which works basically like
//  Bill's SetCookie() function but converts the array to a string prior
//  to actually storing the cookie.
//
//  GetACookie() works exactly the other way 'round: it'll take the
//  string stored in the cookie, converts it back to an array and
//  returns this array.
//
//  To make life easier, the functions GetACookieValue(), SetACookieValue()
//  an DelACookieValue() just work on the named value of an array cookie.
//  For technical reasons please make sure that you do not use these three
//  together with SetACookie()/GetACookie() - you may loose data already
//  stored in your cookie.
//  As written above, SetACookieValue() (and DelACookieValue(), too)
//  immediatly stores a cookie. Since the browser might be set to ask for
//  permission upon storing cookies, the use of multiple SetACookieValue()
//  calls in a script can get quite annoying. If you have to store more
//  than one value in your script, use SetACookie() instead!
//
//  The work is actually done with the help of two all-purpose functions
//  named ArrayToString() and StringToArray(). A third function called
//  ArrayRemove() removes an entry from an array.
//  The Javascript Array object unfortunatly lacks the functionality of
//  these three, and since they simply work on arrays (i.e. not limited
//  to cookie stuff) they might come in handy for other uses as well.
//
//
//  A Note About Cookie Paths
//  -------------------------
//  Note that it is possible to set multiple cookies with the same
//  name but different (nested) paths.  For example:
//
//    SetCookie ("color","red",null,"/outer");
//    SetCookie ("color","blue",null,"/outer/inner");
//
//  However, GetCookie cannot distinguish between these and will return
//  the first cookie that matches a given name.  It is therefore
//  recommended that you *not* use the same name for cookies with
//  different paths.  (Bear in mind that there is *always* a path
//  associated with a cookie; if you don't explicitly specify one,
//  the path of the setting document is used.)
//
//
//  Cookie Dates
//  ------------
//  CreateCookieDate:
//  This version of the script introduces a new function to create cookie
//  expiration dates called CreateCookieDate(). You'll safe two extra
//  calls, including the FixCookieDate() one.
//
//  FixCookieDate:
//  The FixCookieDate function must be called explicitly to correct for
//  the 2.x Mac date bug if you create Date objects yourself. This function
//  should be called *once* after a Date object is created and before it
//  is passed (as an expiration date) to SetCookie. Because the Mac date
//  bug affects all dates, not just those passed to SetCookie, you might
//  want to make it a habit to call FixCookieDate any time you create a
//  new Date object:
//
//    var theDate = new Date();
//    FixCookieDate (theDate);
//
//  Calling FixCookieDate has no effect on platforms other than the
//  Mac, so there is no need to determine the user's platform prior to
//  calling it.
//
//
//  Revision History
//  ----------------
//
//    "Night of the Living Cookie" (25-Jul-96)
//      - DeleteCookie now sets the expiration date to the earliest
//        usable date to get rid of Netscape 2.02 zombie cookies
//      - Optional path and domain parameters to DeleteCookie
//      - Several minor coding improvements
//
//    "Toss Your Cookies" Version (22-Mar-96)
//      - Added FixCookieDate() function to correct for Mac date bug
//
//    "Second Helping" Version (21-Jan-96)
//      - Added path, domain and secure parameters to SetCookie
//      - Replaced home-rolled encode/decode functions with Netscape's
//        new (then) escape and unescape functions
//
//    "Free Cookies" Version (December 95)
//
//
//  Cookie Specs
//  ------------
//  For information on the significance of cookie parameters, and
//  and on cookies in general, please refer to the official cookie
//  spec, at:
//
//      http://www.netscape.com/newsref/std/cookie_spec.html
//
//
//  Example
//  -------

/*

var expdate = new Date ();
FixCookieDate (expdate); // Correct for Mac date bug - call only once for given Date object!
expdate.setTime (expdate.getTime() + (24 * 60 * 60 * 1000)); // 24 hrs from now

-or-

expdate=CreateCookieDate(24 * 60 * 60 * 1000); // 24 hrs from now




SetCookie ("ccpath", "http://www.hidaho.com/colorcenter/", expdate);
SetCookie ("ccname", "hIdaho Design ColorCenter", expdate);
SetCookie ("tempvar", "This is a temporary cookie.");
SetCookie ("ubiquitous", "This cookie will work anywhere in this domain",null,"/");
SetCookie ("paranoid", "This cookie requires secure communications",expdate,"/",null,true);
SetCookie ("goner", "This cookie must die!");

var acookie=new Array("red","green","blue");
acookie["one"]=1; acookie["two"]=2; acookie["three"]=3; acookie["go"]="&=Sönke";
SetACookie("arraycookie",acookie);

document.write ("All cookies: " + document.cookie + "<p>");

DeleteCookie ("goner");

document.write ("Cookie goner deleted: " + document.cookie + "<p>");

document.write ("ccpath = " + GetCookie("ccpath") + "<br>");
document.write ("ccname = " + GetCookie("ccname") + "<br>");
document.write ("tempvar = " + GetCookie("tempvar") + "<br>");

var a=GetACookie("arraycookie");
document.writeln("arraycookie = <ul>");
for (var i in a) document.writeln("<li>"+i+" = "+a[i]);

document.writeln("</ul>arraycookie[two] = " + GetACookieValue("arraycookie","two"));

*/


//******************************************************************


//  [bd]
//  "Internal" function to return the decoded value of a cookie
//

function getCookieVal (offset) {
  var endstr = document.cookie.indexOf (";", offset);
  if (endstr == -1)
    endstr = document.cookie.length;
  return unescape(document.cookie.substring(offset, endstr));
}


//  [bd]
//  Function to correct for 2.x Mac date bug.  Call this function to
//  fix a date object prior to passing it to SetCookie.
//  IMPORTANT:  This function should only be called *once* for
//  any given date object!  See example at the end of this document.
//

function FixCookieDate (date) {
  var base = new Date(0);
  var skew = base.getTime(); // dawn of (Unix) time - should be 0
  if (skew > 0)  // Except on the Mac - ahead of its time
    date.setTime (date.getTime() - skew);
}

//
//  [st]
//  CreateCookieDate(expire) - create cookie expiration date
//
//    expire - Seconds from now until the cookie will be deleted.
//

function CreateCookieDate(expire)
 {
  var expdate = new Date();
  FixCookieDate(expdate);
  expdate.setTime(expdate.getTime() + (expire*1000));
  return expdate;
 };


//  [bd]
//  GetCookie(name) - return the value of the cookie specified by "name".
//
//    name    - String object containing the cookie name.
//    returns - String object containing the cookie value, or null if the
//              cookie does not exist.
//

function GetCookie (name) {
  var arg = name + "=";
  var alen = arg.length;
  var clen = document.cookie.length;
  var i = 0;
  while (i < clen) {
    var j = i + alen;
    if (document.cookie.substring(i, j) == arg)
      return getCookieVal (j);
    i = document.cookie.indexOf(" ", i) + 1;
    if (i == 0) break;
  }
  return null;
}


//  [bd]
//
//  SetCookie(name,value,expires,path,domain,secure) -  create or update a cookie.
//
//    name - String object containing the cookie name.
//    value - String object containing the cookie value.  May contain
//      any valid string characters.
//    [expires] - Date object containing the expiration data of the cookie.  If
//      omitted or null, expires the cookie at the end of the current session.
//    [path] - String object indicating the path for which the cookie is valid.
//      If omitted or null, uses the path of the calling document.
//    [domain] - String object indicating the domain for which the cookie is
//      valid.  If omitted or null, uses the domain of the calling document.
//    [secure] - Boolean (true/false) value indicating whether cookie transmission
//      requires a secure channel (HTTPS).
//
//  The first two parameters are required.  The others, if supplied, must
//  be passed in the order listed above.  To omit an unused optional field,
//  use null as a place holder.  For example, to call SetCookie using name,
//  value and path, you would code:
//
//      SetCookie ("myCookieName", "myCookieValue", null, "/");
//
//  Note that trailing omitted parameters do not require a placeholder.
//
//  To set a secure cookie for path "/myPath", that expires after the
//  current session, you might code:
//
//      SetCookie (myCookieVar, cookieValueVar, null, "/myPath", null, true);
//

function SetCookie (name,value,expires,path,domain,secure) {
  document.cookie = name + "=" + escape (value) +
    ((expires) ? "; expires=" + expires.toGMTString() : "") +
    ((path) ? "; path=" + path : "") +
    ((domain) ? "; domain=" + domain : "") +
    ((secure) ? "; secure" : "");
}


//  [bd]
//
//  DeleteCookie(name,path,domain) - delete a cookie.
//
//    name -   String object containing the cookie name
//    path -   String object containing the path of the cookie to delete.
//             This MUST be the same as the path used to create the cookie, or
//             null/omitted if no path was specified when creating the cookie.
//    domain - String object containing the domain of the cookie to delete.
//             This MUST be the same as the domain used to create the cookie,
//             or null/omitted if no domain was specified when creating the
//             cookie.
//
//  This function actually sets the cookie's expiration date to the start of
//  epoch and clears its contents.
//

function DeleteCookie (name,path,domain) {
  if (GetCookie(name)) {
    document.cookie = name + "=" +
      ((path) ? "; path=" + path : "") +
      ((domain) ? "; domain=" + domain : "") +
      "; expires=Thu, 01-Jan-70 00:00:01 GMT";
  };
}


//
// Functions to store multiple values in one cookie (an 'arraycookie')
//

//  [st]
//
//  ArrayToString(array) - convert array to string
//
//    array   - An array object to converted into a string.
//    returns - A string representation of array.
//

function ArrayToString(a)
 {
  var s="";
  for (var i in a)
   {
    s+=("&"+i+"="+escape(a[i]));
   };
  return s;
 };

//  [st]
//
//  StringToArray(string) - convert string to array
//
//    string  - String created by ArrayToString()
//    returns - An array object containing the values from the string.
//

function StringToArray(s)
 {
  var a=new Array();
  var start=s.indexOf("&")
  if(start != 0) 
  {
	s = "&" + s;
	start = 0
  }
  
  while (start>=0)
   {
    start++;
    var gleich=s.indexOf("=",start);
    var ende=s.indexOf("&",gleich);
    if (ende<0) ende=s.length;
    a[s.substring(start,gleich)]=unescape(s.substring(gleich+1,ende));
    start=s.indexOf("&",ende)
   };
  return a;
 };


//  [st]
//
//  ArrayRemove(array,entry) - remove entry from array
//
//    a       - Array object to remove an entry from.
//    e       - Name of the entry to be removed.
//    returns - A copy of the submitted array without the named entry.
//

function ArrayRemove(a,e)
 {
  var n=new Array();
  for (var i in a)
   {
    if (i!=e)
       n[i]=a[i];
   };
  return n;
 };


//  [st]
//
//  SetACookie(name,arr,expires,path,domain,secure) - set array cookie
//
//  Parameters are the same as as in SetCookie() above, except for arr, which
//  is an Array object containing all the values to be stored in a cookie.
//
//  SetACookie stores array 'arr' in a cookie named 'name'. If 'arr' is
//  empty, the cookie will be deleted.
//

function SetACookie(name,arr,expires,path,domain,secure)
 {
  var s=ArrayToString(arr); 
  if (s.length>0) // arr.length doesn't work for this type of array
     SetCookie(name,ArrayToString(arr),expires,path,domain,secure);
   else
     DeleteCookie(name,path,domain);
 };


//  [st]
//
//  GetACookie(name) - get array cookie
//
//    name    - String containing the cookie name.
//    returns - Array containing all values from the cookie or null, if
//              the named cookie does not exist.
//

function GetACookie(name)
 {
  var c=GetCookie(name);
  if (c)
     return StringToArray(c);
   else
     return null;
 };


//  [st]
//
//  GetACookieValue(name,value) - get single value from array cookie
//
//    name    - String containing the cookie name.
//    value   - String containing the name of the value to be read from
//              this cookie.
//    returns - the requested value or null, if it does not exist.
//

function GetACookieValue(name,value)
 {
  var ca=GetACookie(name);
  if (ca)
     return ca[value];
   else
     return null;
 };

//  [st]
//
//  SetACookieValue(name,value,data,expires,path,domain,secure)
//  - set single value from array cookie
//
//    name    - String containing the cookie name.
//    value   - String containing the name of the value to be written
//              into this cookie.
//    data    - Data to be written into this cookie.
//
//    expires,path,domain,secure - See SetCookie().
//
//  Set a specific value in an array cookie.
//
//  Note 1: This function exists only to make life a bit easier if you
//  have to deal just once with just a single value in your script. If
//  you need to work with more values, use SetACookie() since for each
//  call of SetACookieValue() a cookie will be stored right away, where
//  each time the browser possibly asks the user for permission.
//  This is one of the best ways to scare users away..
//
//  Note 2: This function may interfere with SetACookie/GetACookie, since
//  it to work on its own copy of the cookie! Do not do the following:
//
//    ca=GetACookie("bla")
//    SetACookieValue("bla","some_value_name","some_data")
//    SetACookie("bla",ca)
//

function SetACookieValue(name,value,data,expires,path,domain,secure)
 {
  var ca=GetACookie(name);
  if (! ca)
     ca=new Array();
  ca[value]=data;
  SetACookie(name,ca,expires,path,domain,secure);
 };


//  [st]
//
//  DelACookieValue(name,value,expires,path,domain,secure)
//  - delete single value from array cookie
//
//    name    - String containing the cookie name.
//    value   - String containing the name of the value to be deleted
//              from this cookie.
//
//    expires,path,domain,secure - See SetCookie().
//
//  Delete a specific value in an array cookie.
//
//  Note 1: This function exists only to make life a bit easier if you
//  have to deal just once with just a single value in your script. If
//  you need to work with more values, use SetACookie() since for each
//  call of DelACookieValue() a cookie will be stored right away, where
//  each time the browser possibly asks the user for permission.
//  And this is one of the best ways to scare users away..
//
//  Note 2: This function may interfere with SetACookie/GetACookie, since
//  it to work on its own copy of the cookie! Do not do the following:
//
//    ca=GetACookie("bla")
//    DelACookieValue("bla","some_value_name")
//    SetACookie("bla",ca)
//

function DelACookieValue(name,value,expires,path,domain,secure)
 {
  var ca=GetACookie(name);
  if (ca)
    {
     ArrayRemove(ca,value);
     SetACookie(name,ca,expires,path,domain,secure);
    };
 };


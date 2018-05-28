---
layout: docs
title:  "Squid Tutorial"
section: "tutorial"
position: 2
---

The Squid tutorial is divided into several sections:

{% assign sorted = site.pages | sort: 'subposition' %}
{% for x in sorted %}
  {% if x.section == 'tutorial' and x.title != page.title %}
 * [{{x.title}}]({{site.baseurl}}{{x.url}})
  {% endif %}
{% endfor %}

<!-- Q: why does {{x.position}} not work?  -->

should = require('chai').should()
Browser = require 'zombie'

port = 8080
port = process.env.PORT if process.env.PORT

home = "http://localhost:#{port}/"
planning = "#{home}planning.html"

newBrowser = ->
  new Browser({ "maxWait": 10000, "waitFor": 10000 })

#unstar to debug
#Browser.debug=true

describe 'Planning', ->
  it 'should show the title', (done) ->
    browser = newBrowser()
    browser.visit home, ->
      browser.text('title').should.equal 'CodeStory - Devoxx Planning'
      done()

  it 'should show toc', (done) ->
    browser = newBrowser()
    browser.visit planning, ->
      browser.text('.toc a').should.contain '09:30'
      done()

  it 'should show sessions', (done) ->
    browser = newBrowser()
    browser.visit planning, ->
      browser.text('#talk-1253 h2').should.equal 'CodeStory 2013 (Conference)'
      browser.text('#talk-1253 .speaker').should.equal 'David Gageot, Jean-laurent De morlhon @Auditorium Vendredi de 17:00 à 17:50'
      browser.text('#talk-1253 p').should.contain 'CodeStory 2013'
      done()

  it 'should redirect to authentication when user star', (done) ->
    browser = newBrowser()
    browser.visit planning, ->
      browser.clickLink '#talk-1253 .star', ->
        browser.location.href.should.contain '/planning.html'
        browser.text('#auth a').should.equal 'Déconnexion'
        done()

  it 'should star while logged in', (done) ->
    browser = newBrowser()
    browser.visit planning, ->
      browser.text('#auth a').should.equal 'Se connecter'
      should.not.exist browser.cookies().get('userId')
      browser.query('#talk-1253 .star').should.be.ok
      browser.query('#talk-1241 .star').should.be.ok
      browser.query('#talk-1242 .star').should.be.ok
      should.not.exist browser.query('#talk-759 .starred')
      should.not.exist browser.query('#talk-760 .starred')
      should.not.exist browser.query('#talk-761 .starred')

      browser.clickLink '#login', ->
        browser.text('#auth a').should.equal 'Déconnexion'
        browser.text('#screenName').should.equal '@arnold'
        browser.cookies().get('userId').should.equal '42'
        browser.cookies().get('screenName').should.equal 'arnold'
        browser.query('#talk-1253 .star').should.be.ok
        should.not.exist browser.query('#talk-759 .starred')
        browser.clickLink '#talk-1253 .star', ->
          browser.query('#talk-1253 .starred').should.be.ok
          browser.clickLink '#talk-1253 .star', ->
            browser.query('#talk-1253 .star').should.be.ok
            should.not.exist browser.query '#talk-1253 .starred'
            browser.clickLink '#logout', ->
              should.not.exist browser.cookies().get('userId')
              browser.text('#auth a').should.equal 'Se connecter'
              done()

  it 'Should filter with Url', (done) ->
    browser = newBrowser()
    browser.visit "#{planning}?q=foo", ->
      browser.query('#search_box').value.should.equal 'foo'
      done()
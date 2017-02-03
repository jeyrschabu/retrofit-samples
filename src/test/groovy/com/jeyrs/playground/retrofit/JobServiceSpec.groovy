package com.jeyrs.playground.retrofit

import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.http.Path
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import spock.lang.Shared
import spock.lang.Specification

class JobServiceSpec extends Specification {
  static final String BASE_URL = "http://url.com"

  @Shared
  NetworkBehavior behavior = NetworkBehavior.create()

  @Shared
  JobService mockJobService

  static class MockJobService implements JobService {

    BehaviorDelegate<JobService> delegate
    Map job
    MockJobService(BehaviorDelegate<JobService> delegate) {
      this.delegate = delegate;
      this.job = [:]
    }

    @Override
    Call<Map> submit(@Path("name") String name) {
      delegate.returningResponse([ name: name]).submit(name)
    }
  }

  def setupSpec() {
    Retrofit retrofit = new Retrofit.Builder()
      .baseUrl(BASE_URL).build()

    def mockRetrofit = new MockRetrofit.Builder(retrofit)
      .networkBehavior(behavior).build()

    BehaviorDelegate<JobService> delegate = mockRetrofit.create(JobService)


    mockJobService = new MockJobService(delegate);
  }

  def "should succeed" () {
    given:
    def response = mockJobService.submit("testJob").execute()

    expect:
    response.isSuccessful()
    response.body() == [name : "testJob"]
  }

  def "should fail on 500" () {
    given:
    behavior.setFailurePercent(100);

    when:
    mockJobService.submit("testJob").execute()

    then:
    thrown(IOException)
  }
}

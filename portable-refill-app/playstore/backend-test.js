/**
 * Backend API Testing Script
 * Tests user registration, login, and all authenticated endpoints
 */

const https = require('https');
const http = require('http');

// Configuration - UPDATE THIS WITH YOUR ACTUAL API URL
const API_BASE_URL = 'https://cp1.interion.com.sg'; // Your production API
const USE_HTTPS = API_BASE_URL.startsWith('https');

// Test user data
const testUser = {
  email: `test${Date.now()}@example.com`,
  password: 'Test@123456',
  name: 'Test User',
  phone: '+60123456789'
};

let authToken = '';
let userId = '';

/**
 * Make HTTP request
 */
function makeRequest(method, path, data = null, token = null) {
  return new Promise((resolve, reject) => {
    const url = new URL(API_BASE_URL + path);
    const client = USE_HTTPS ? https : http;
    
    const options = {
      hostname: url.hostname,
      port: url.port || (USE_HTTPS ? 443 : 80),
      path: url.pathname + url.search,
      method: method,
      headers: {
        'Content-Type': 'application/json',
        ...(token && { 'Authorization': `Bearer ${token}` })
      }
    };

    const req = client.request(options, (res) => {
      let body = '';
      res.on('data', (chunk) => body += chunk);
      res.on('end', () => {
        try {
          const response = JSON.parse(body);
          resolve({ status: res.statusCode, data: response });
        } catch (e) {
          resolve({ status: res.statusCode, data: body });
        }
      });
    });

    req.on('error', (error) => reject(error));
    
    if (data) {
      req.write(JSON.stringify(data));
    }
    
    req.end();
  });
}

/**
 * Test functions
 */
async function testRegister() {
  console.log('\n📝 Testing User Registration...');
  try {
    const response = await makeRequest('POST', '/auth/register', {
      email: testUser.email,
      password: testUser.password,
      name: testUser.name,
      phone: testUser.phone
    });

    if (response.status === 200 || response.status === 201) {
      // API returns token in data.data.token or data.token
      authToken = response.data?.data?.token || response.data?.token;
      userId = response.data.user?.id;
      console.log('✅ Registration successful');
      console.log(`   User ID: ${userId}`);
      console.log(`   Email: ${testUser.email}`);
      return true;
    } else {
      console.log(`❌ Registration failed: ${response.status}`);
      console.log(`   Response:`, response.data);
      return false;
    }
  } catch (error) {
    console.log(`❌ Registration error: ${error.message}`);
    return false;
  }
}

async function testLogin() {
  console.log('\n🔐 Testing User Login...');
  try {
    const response = await makeRequest('POST', '/auth/login', {
      email: testUser.email,
      password: testUser.password
    });

    if (response.status === 200) {
      // API returns token in data.data.token or data.token
      authToken = response.data?.data?.token || response.data?.token;
      if (authToken) {
        console.log('✅ Login successful');
        console.log(`   Token received: ${authToken.substring(0, 20)}...`);
        return true;
      } else {
        console.log('❌ Login failed: No token in response');
        console.log(`   Response:`, response.data);
        return false;
      }
    } else {
      console.log(`❌ Login failed: ${response.status}`);
      console.log(`   Response:`, response.data);
      return false;
    }
  } catch (error) {
    console.log(`❌ Login error: ${error.message}`);
    return false;
  }
}

async function testGetProfile() {
  console.log('\n👤 Testing Get Profile...');
  try {
    const response = await makeRequest('GET', '/auth/me', null, authToken);

    if (response.status === 200) {
      console.log('✅ Get profile successful');
      console.log(`   User: ${response.data.name || response.data.username}`);
      console.log(`   Email: ${response.data.email || response.data.useremail}`);
      console.log(`   Role: ${response.data.role || response.data.userrole}`);
      return true;
    } else {
      console.log(`❌ Get profile failed: ${response.status}`);
      console.log(`   Response:`, response.data);
      return false;
    }
  } catch (error) {
    console.log(`❌ Get profile error: ${error.message}`);
    return false;
  }
}

async function testUpdateProfile() {
  console.log('\n✏️ Testing Update Profile...');
  try {
    const response = await makeRequest('POST', '/user/update', {
      name: 'Updated Test User',
      phone: '+60187654321'
    }, authToken);

    if (response.status === 200) {
      console.log('✅ Update profile successful');
      console.log(`   Updated name: ${response.data.name || response.data.username}`);
      console.log(`   Updated phone: ${response.data.phone || response.data.userphone}`);
      return true;
    } else {
      console.log(`❌ Update profile failed: ${response.status}`);
      console.log(`   Response:`, response.data);
      return false;
    }
  } catch (error) {
    console.log(`❌ Update profile error: ${error.message}`);
    return false;
  }
}

async function testGetStations() {
  console.log('\n🏭 Testing Get Stations...');
  try {
    const response = await makeRequest('GET', '/stations', null, authToken);

    if (response.status === 200) {
      console.log('✅ Get stations successful');
      console.log(`   Found ${response.data.length || 0} stations`);
      if (response.data.length > 0) {
        console.log(`   First station: ${response.data[0].stationname}`);
      }
      return true;
    } else {
      console.log(`❌ Get stations failed: ${response.status}`);
      console.log(`   Response:`, response.data);
      return false;
    }
  } catch (error) {
    console.log(`❌ Get stations error: ${error.message}`);
    return false;
  }
}

async function testGetTransactions() {
  console.log('\n💳 Testing Get Transactions...');
  try {
    const response = await makeRequest('GET', '/transactions', null, authToken);

    if (response.status === 200) {
      console.log('✅ Get transactions successful');
      console.log(`   Found ${response.data.length || 0} transactions`);
      return true;
    } else {
      console.log(`❌ Get transactions failed: ${response.status}`);
      console.log(`   Response:`, response.data);
      return false;
    }
  } catch (error) {
    console.log(`❌ Get transactions error: ${error.message}`);
    return false;
  }
}

async function testChangePassword() {
  console.log('\n🔑 Testing Change Password...');
  try {
    const newPassword = 'NewTest@123456';
    const response = await makeRequest('POST', '/user/change-password', {
      currentPassword: testUser.password,
      newPassword: newPassword
    }, authToken);

    if (response.status === 200) {
      console.log('✅ Change password successful');
      
      // Test login with new password
      console.log('   Testing login with new password...');
      const loginResponse = await makeRequest('POST', '/auth/login', {
        email: testUser.email,
        password: newPassword
      });
      
      if (loginResponse.status === 200) {
        console.log('   ✅ Login with new password successful');
        authToken = loginResponse.data.token;
        return true;
      } else {
        console.log('   ❌ Login with new password failed');
        return false;
      }
    } else {
      console.log(`❌ Change password failed: ${response.status}`);
      console.log(`   Response:`, response.data);
      return false;
    }
  } catch (error) {
    console.log(`❌ Change password error: ${error.message}`);
    return false;
  }
}

/**
 * Run all tests
 */
async function runTests() {
  console.log('═══════════════════════════════════════════════════');
  console.log('🚀 AceRev Backend API Testing');
  console.log('═══════════════════════════════════════════════════');
  console.log(`API URL: ${API_BASE_URL}`);
  console.log(`Test Email: ${testUser.email}`);
  
  const results = {
    passed: 0,
    failed: 0,
    total: 0
  };

  // Test sequence
  const tests = [
    { name: 'User Registration', fn: testRegister },
    { name: 'User Login', fn: testLogin },
    { name: 'Get Profile', fn: testGetProfile },
    { name: 'Update Profile', fn: testUpdateProfile },
    { name: 'Get Stations', fn: testGetStations },
    { name: 'Get Transactions', fn: testGetTransactions },
    { name: 'Change Password', fn: testChangePassword }
  ];

  for (const test of tests) {
    results.total++;
    const passed = await test.fn();
    if (passed) {
      results.passed++;
    } else {
      results.failed++;
    }
    
    // Wait between tests
    await new Promise(resolve => setTimeout(resolve, 500));
  }

  // Summary
  console.log('\n═══════════════════════════════════════════════════');
  console.log('📊 Test Summary');
  console.log('═══════════════════════════════════════════════════');
  console.log(`Total Tests: ${results.total}`);
  console.log(`✅ Passed: ${results.passed}`);
  console.log(`❌ Failed: ${results.failed}`);
  console.log(`Success Rate: ${((results.passed / results.total) * 100).toFixed(1)}%`);
  console.log('═══════════════════════════════════════════════════');

  if (results.failed === 0) {
    console.log('\n🎉 All tests passed! Your backend is working correctly.');
  } else {
    console.log('\n⚠️ Some tests failed. Please check the backend configuration and deployment.');
  }
}

// Check if API_BASE_URL is configured
if (API_BASE_URL === 'https://api.example.com') {
  console.log('═══════════════════════════════════════════════════');
  console.log('⚠️  CONFIGURATION REQUIRED');
  console.log('═══════════════════════════════════════════════════');
  console.log('Please update the API_BASE_URL in this script with your actual backend URL.');
  console.log('\nTo find your API URL:');
  console.log('1. Check AWS API Gateway console');
  console.log('2. Look for your API endpoint URL');
  console.log('3. Update line 11 of this script');
  console.log('\nExample: const API_BASE_URL = \'https://abc123.execute-api.ap-southeast-1.amazonaws.com/prod\';');
  console.log('═══════════════════════════════════════════════════');
} else {
  runTests().catch(console.error);
}

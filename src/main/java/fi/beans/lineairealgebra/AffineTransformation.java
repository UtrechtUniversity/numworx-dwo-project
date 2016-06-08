package fi.beans.lineairealgebra;

public class AffineTransformation {
	static final int cVectorSize = 2;
//	
//	Point2dList src;
//	Point2dList dest;
//	// Matrix
//	
//	public AffineTransformation() {
//		src = null;
//		dest = null;
////		Matrix = null;
//	}
//	
//	public void setSource(Point2dList src) {
//		this.src = src;
//	}
//	
//	public void setDestination(Point2dList dest) {
//		this.dest = dest;
//	}
	
//	def Affine_Fit( from_pts, to_pts ):
//	    """Fit an affine transformation to given point sets.
//	      More precisely: solve (least squares fit) matrix 'A'and 't' from
//	      'p ~= A*q+t', given vectors 'p' and 'q'.
//	      Works with arbitrary dimensional vectors (2d, 3d, 4d...).
//
//	      Written by Jarno Elonen <elonen@iki.fi> in 2007.
//	      Placed in Public Domain.
//
//	      Based on paper "Fitting affine and orthogonal transformations
//	      between two sets of points, by Helmuth Späth (2003)."""
//
//	    q = from_pts
//	    p = to_pts
//	    if len(q) != len(p) or len(q)<1:
//	        print "from_pts and to_pts must be of same size."
//	        return false
//
//	    dim = len(q[0]) # num of dimensions
//	    if len(q) < dim:
//	        print "Too few points => under-determined system."
//	        return false
//
//	    # Make an empty (dim) x (dim+1) matrix and fill it
//	    c = [[0.0 for a in range(dim)] for i in range(dim+1)]
//	    for j in range(dim):
//	        for k in range(dim+1):
//	            for i in range(len(q)):
//	                qt = list(q[i]) + [1]
//	                c[k][j] += qt[k] * p[i][j]
//
//	    # Make an empty (dim+1) x (dim+1) matrix and fill it
//	    Q = [[0.0 for a in range(dim)] + [0] for i in range(dim+1)]
//	    for qi in q:
//	        qt = list(qi) + [1]
//	        for i in range(dim+1):
//	            for j in range(dim+1):
//	                Q[i][j] += qt[i] * qt[j]
//
//	    # Ultra simple linear system solver. Replace this if you need speed.
//	    def gauss_jordan(m, eps = 1.0/(10**10)):
//	      """Puts given matrix (2D array) into the Reduced Row Echelon Form.
//	         Returns True if successful, False if 'm' is singular.
//	         NOTE: make sure all the matrix items support fractions! Int matrix will NOT work!
//	         Written by Jarno Elonen in April 2005, released into Public Domain"""
//	      (h, w) = (len(m), len(m[0]))
//	      for y in range(0,h):
//	        maxrow = y
//	        for y2 in range(y+1, h):    # Find max pivot
//	          if abs(m[y2][y]) > abs(m[maxrow][y]):
//	            maxrow = y2
//	        (m[y], m[maxrow]) = (m[maxrow], m[y])
//	        if abs(m[y][y]) <= eps:     # Singular?
//	          return False
//	        for y2 in range(y+1, h):    # Eliminate column y
//	          c = m[y2][y] / m[y][y]
//	          for x in range(y, w):
//	            m[y2][x] -= m[y][x] * c
//	      for y in range(h-1, 0-1, -1): # Backsubstitute
//	        c  = m[y][y]
//	        for y2 in range(0,y):
//	          for x in range(w-1, y-1, -1):
//	            m[y2][x] -=  m[y][x] * m[y2][y] / c
//	        m[y][y] /= c
//	        for x in range(h, w):       # Normalize row y
//	          m[y][x] /= c
//	      return True
//
//	    # Augement Q with c and solve Q * a' = c by Gauss-Jordan
//	    M = [ Q[i] + c[i] for i in range(dim+1)]
//	    if not gauss_jordan(M):
//	        print "Error: singular matrix. Points are probably coplanar."
//	        return false
//
//	    # Make a result object
//	    class Transformation:
//	        """Result object that represents the transformation
//	           from affine fitter."""
//
//	        def To_Str(self):
//	            res = ""
//	            for j in range(dim):
//	                str = "x%d' = " % j
//	                for i in range(dim):
//	                    str +="x%d * %f + " % (i, M[i][j+dim+1])
//	                str += "%f" % M[dim][j+dim+1]
//	                res += str + "\n"
//	            return res
//
//	        def Transform(self, pt):
//	            res = [0.0 for a in range(dim)]
//	            for j in range(dim):
//	                for i in range(dim):
//	                    res[j] += pt[i] * M[i][j+dim+1]
//	                res[j] += M[dim][j+dim+1]
//	            return res
//	    return Transformation()
	
//	# Input: expects Nx3 matrix of points
//	# Returns R,t
//	# R = 3x3 rotation matrix
//	# t = 3x1 column vector
//
//	def rigid_transform_3D(A, B):
//	    assert len(A) == len(B)
//
//	    N = A.shape[0]; # total points
//
//	    centroid_A = mean(A, axis=0)
//	    centroid_B = mean(B, axis=0)
//	    
//	    # centre the points
//	    AA = A - tile(centroid_A, (N, 1))
//	    BB = B - tile(centroid_B, (N, 1))
//
//	    # dot is matrix multiplication for array
//	    H = transpose(AA) * BB
//
//	    U, S, Vt = linalg.svd(H)
//
//	    R = Vt.T * U.T
//
//	    # special reflection case
//	    if linalg.det(R) < 0:
//	       print "Reflection detected"
//	       Vt[2,:] *= -1
//	       R = Vt.T * U.T
//
//	    t = -R*centroid_A.T + centroid_B.T
//
//	    print t
//
//	    return R, t
//
//	# Test with random data
//
//	# Random rotation and translation
//	R = mat(random.rand(3,3))
//	t = mat(random.rand(3,1))
//
//	# make R a proper rotation matrix, force orthonormal
//	U, S, Vt = linalg.svd(R)
//	R = U*Vt
//
//	# remove reflection
//	if linalg.det(R) < 0:
//	   Vt[2,:] *= -1
//	   R = U*Vt
//
//	# number of points
//	n = 10
//
//	A = mat(random.rand(n,3));
//	B = R*A.T + tile(t, (1, n))
//	B = B.T;
//
//	# recover the transformation
//	ret_R, ret_t = rigid_transform_3D(A, B)
//
//	A2 = (ret_R*A.T) + tile(ret_t, (1, n))
//	A2 = A2.T
//
//	# Find the error
//	err = A2 - B
//
//	err = multiply(err, err)
//	err = sum(err)
//	rmse = sqrt(err/n);
//
//	print "Points A"
//	print A
//	print ""
//
//	print "Points B"
//	print B
//	print ""
//
//	print "Rotation"
//	print R
//	print ""
//
//	print "Translation"
//	print t
//	print ""
//
//	print "RMSE:", rmse
//	print "If RMSE is near zero, the function is correct!"
	
}



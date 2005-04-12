/* 
Copyright (c) 1994-1999 Pixware. 
This file is part of the Pixware xs library.
For conditions of distribution and use, see the accompanying legal.txt file.
*/

/*
   _________________________________________________________________________

   Constants and types required by both the C and C++ xstDataAccess API
   _________________________________________________________________________
*/

/*xConstants and types related to class {{xstDataAccess}} */

/*x
  Macros:

~~x
*/
#define xskMaxBookmarkTypes 		8
#define xskAttributeNameMaxLength 	39
#define xskMaxUserTypes			100
#define xskUserTypeNameMaxLength 	39

/*x {xstUserType} */
typedef struct {
    char 		  fName[xskUserTypeNameMaxLength+1];
    xstUserTypeCloneProc  fCloneProc;
    xstUserTypeDeleteProc fDeleteProc;
    xstUserTypeReadProc   fReadProc;
    xstUserTypeWriteProc  fWriteProc;
    xstUserTypePrintProc  fPrintProc;
    void*		  fProcData;
} xstUserType;

/*
   _________________________________________________________________________

   xstDataAccess
   _________________________________________________________________________
*/

/*x

Class {xstDataAccess}
~~~~~~~~~~~~~~~~~~~~~

  Abstract class which models the access to a serie of numerical values 
  (samples) and to their associated attributes and marks.

  Attributes are used to specify the semantics of samples.

  Marks are used to specify a range of sample positions and/or ``interesting'' 
  sample positions. There are 3 kinds of marks:

    [Point] the insertion point.

    [Mark] the selection mark. 
 
           A data set (even empty) always has a point and a mark.

           The selection is the subset of samples located between point 
           and mark. More precisely: if, for instance, point is located 
           before mark, the selection is the subset of samples located 
           between point and mark, <point included and mark excluded>.

    [Bookmarks] are used to specify ``interesting'' sample positions. 

                There are xskMaxBookmarkTypes types of bookmarks. A newly 
                created data set has no bookmarks at all. One may add as 
                many bookmarks (of any of the xskMaxBookmarkTypes) types as
                he/she wants.
*/

class xsDLL_TAG xstDataAccess {
public:
    virtual ~xstDataAccess() {/*NOTHING*/};

/*x
* Access to samples
~~~~~~~~~~~~~~~~~~~

~~x

  {xstDataAccess::DataType} returns the numerical type of the samples.
*/
    virtual xstDataType DataType();

/*x
  {xstDataAccess::SizeOfDataType} returns the size in bytes (like sizeof)
  of the samples.
*/
    size_t SizeOfDataType();

    /**
     ** Access to marks
     *~~~~~~~~~~~~~~~~~
     *
     *~~x
     *
     *  {xstDataAccess::Point} returns the insertion point (a sample position).
     *
     *  Note that a point equal to this->Size() is a valid position which 
     *  means after last sample.
     */
    virtual int Point();

    /**
     *  {xstDataAccess::Mark} returns the selection mark (a sample position).
     *
     *  Note that a mark equal to this->Size() is a valid position which means 
     *  after last sample.
     */
    virtual int Mark();

    /**
     ** Convenience functions for accessing samples
     *~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     *~~x
     *
     *  {xstDataAccess::GetSample} returns in <value> the value of 
     *  sample #<index>.
     */
    utStatus GetSample(int index, xstAnyValue& value);
    utStatus GetSample(int index, float& value);

    /**  Another one */
    utStatus GetSample(int index, double& value);

    /**
     *  {xstDataAccess::GetSamples} works like {{xstDataAccess::Get}} but 
     *  automatically converts samples read from the data set to float or
     *  double values.
     */
    utStatus GetSamples(int index, float* values, int& count);
    utStatus GetSamples(int index, double* values, int& count);

    //x
    //* Convenience functions for accessing attributes
    //~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
    //
    //~~x
    //
    //  {xstDataAccess::GetStringAttribute} returns in <string> the value of 
    //  attribute called <name>. If such attribute is found and actually is a 
    //  string, this function returns true. Otherwise, it returns false and 
    //  <string> is set to "".
    //
    utBool GetStringAttribute(const char*  name, const char*& string);

    //x
    //  {xstDataAccess::GetNumericAttribute} returns in <number> the value of 
    //  attribute called <name>. If such attribute is found and actually is a 
    //  number, this function returns true. Otherwise, it returns false and 
    //  <number> is set to (utInt32)0.
    utBool GetNumericAttribute(const char*  name, xstAnyValue& number);

    //x  Another one.
    utBool GetNumericAttribute(const char* name, double& number);

//x
//  {xstDataAccess::GetName} works like {{xstDataAccess::GetStringAttribute}}
//  and returns in <name> the value of attribute xskName if any or "".
//

    //x  Single line.

};




package org.apache.maven.doxia.module.rtf;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

/**
 * @version $Id$
 */
class SerifItalic
    extends FontMetrics
{
    static final CharMetrics[] metrics = {new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 333, 0, 39, -11, 302, 667 ), new CharMetrics( 420, 0, 144, 421, 432, 666 ),
        new CharMetrics( 500, 0, 2, 0, 540, 676 ), new CharMetrics( 500, 0, 31, -89, 497, 731 ),
        new CharMetrics( 833, 0, 79, -13, 790, 676 ), new CharMetrics( 778, 0, 76, -18, 723, 666 ),
        new CharMetrics( 333, 0, 151, 436, 290, 666 ), new CharMetrics( 333, 0, 42, -181, 315, 669 ),
        new CharMetrics( 333, 0, 16, -180, 289, 669 ), new CharMetrics( 500, 0, 128, 255, 492, 666 ),
        new CharMetrics( 675, 0, 86, 0, 590, 506 ), new CharMetrics( 250, 0, -4, -129, 135, 101 ),
        new CharMetrics( 333, 0, 49, 192, 282, 255 ), new CharMetrics( 250, 0, 27, -11, 138, 100 ),
        new CharMetrics( 278, 0, -65, -18, 386, 666 ), new CharMetrics( 500, 0, 32, -7, 497, 676 ),
        new CharMetrics( 500, 0, 49, 0, 409, 676 ), new CharMetrics( 500, 0, 12, 0, 452, 676 ),
        new CharMetrics( 500, 0, 15, -7, 465, 676 ), new CharMetrics( 500, 0, 1, 0, 479, 676 ),
        new CharMetrics( 500, 0, 15, -7, 491, 666 ), new CharMetrics( 500, 0, 30, -7, 521, 686 ),
        new CharMetrics( 500, 0, 75, -8, 537, 666 ), new CharMetrics( 500, 0, 30, -7, 493, 676 ),
        new CharMetrics( 500, 0, 23, -17, 492, 676 ), new CharMetrics( 333, 0, 50, -11, 261, 441 ),
        new CharMetrics( 333, 0, 27, -129, 261, 441 ), new CharMetrics( 675, 0, 84, -8, 592, 514 ),
        new CharMetrics( 675, 0, 86, 120, 590, 386 ), new CharMetrics( 675, 0, 84, -8, 592, 514 ),
        new CharMetrics( 500, 0, 132, -12, 472, 664 ), new CharMetrics( 920, 0, 118, -18, 806, 666 ),
        new CharMetrics( 611, 0, -51, 0, 564, 668 ), new CharMetrics( 611, 0, -8, 0, 588, 653 ),
        new CharMetrics( 667, 0, 66, -18, 689, 666 ), new CharMetrics( 722, 0, -8, 0, 700, 653 ),
        new CharMetrics( 611, 0, -1, 0, 634, 653 ), new CharMetrics( 611, 0, 8, 0, 645, 653 ),
        new CharMetrics( 722, 0, 52, -18, 722, 666 ), new CharMetrics( 722, 0, -8, 0, 767, 653 ),
        new CharMetrics( 333, 0, -8, 0, 384, 653 ), new CharMetrics( 444, 0, -6, -18, 491, 653 ),
        new CharMetrics( 667, 0, 7, 0, 722, 653 ), new CharMetrics( 556, 0, -8, 0, 559, 653 ),
        new CharMetrics( 833, 0, -18, 0, 873, 653 ), new CharMetrics( 667, 0, -20, -15, 727, 653 ),
        new CharMetrics( 722, 0, 60, -18, 699, 666 ), new CharMetrics( 611, 0, 0, 0, 605, 653 ),
        new CharMetrics( 722, 0, 59, -182, 699, 666 ), new CharMetrics( 611, 0, -13, 0, 588, 653 ),
        new CharMetrics( 500, 0, 17, -18, 508, 667 ), new CharMetrics( 556, 0, 59, 0, 633, 653 ),
        new CharMetrics( 722, 0, 102, -18, 765, 653 ), new CharMetrics( 611, 0, 76, -18, 688, 653 ),
        new CharMetrics( 833, 0, 71, -18, 906, 653 ), new CharMetrics( 611, 0, -29, 0, 655, 653 ),
        new CharMetrics( 556, 0, 78, 0, 633, 653 ), new CharMetrics( 556, 0, -6, 0, 606, 653 ),
        new CharMetrics( 389, 0, 21, -153, 391, 663 ), new CharMetrics( 278, 0, -41, -18, 319, 666 ),
        new CharMetrics( 389, 0, 12, -153, 382, 663 ), new CharMetrics( 422, 0, 0, 301, 422, 666 ),
        new CharMetrics( 500, 0, 0, -125, 500, -75 ), new CharMetrics( 333, 0, 171, 436, 310, 666 ),
        new CharMetrics( 500, 0, 17, -11, 476, 441 ), new CharMetrics( 500, 0, 23, -11, 473, 683 ),
        new CharMetrics( 444, 0, 30, -11, 425, 441 ), new CharMetrics( 500, 0, 15, -13, 527, 683 ),
        new CharMetrics( 444, 0, 31, -11, 412, 441 ), new CharMetrics( 278, 0, -147, -207, 424, 678 ),
        new CharMetrics( 500, 0, 8, -206, 472, 441 ), new CharMetrics( 500, 0, 19, -9, 478, 683 ),
        new CharMetrics( 278, 0, 49, -11, 264, 654 ), new CharMetrics( 278, 0, -124, -207, 276, 654 ),
        new CharMetrics( 444, 0, 14, -11, 461, 683 ), new CharMetrics( 278, 0, 41, -11, 279, 683 ),
        new CharMetrics( 722, 0, 12, -9, 704, 441 ), new CharMetrics( 500, 0, 14, -9, 474, 441 ),
        new CharMetrics( 500, 0, 27, -11, 468, 441 ), new CharMetrics( 500, 0, -75, -205, 469, 441 ),
        new CharMetrics( 500, 0, 25, -209, 483, 441 ), new CharMetrics( 389, 0, 45, 0, 412, 441 ),
        new CharMetrics( 389, 0, 16, -13, 366, 442 ), new CharMetrics( 278, 0, 37, -11, 296, 546 ),
        new CharMetrics( 500, 0, 42, -11, 475, 441 ), new CharMetrics( 444, 0, 21, -18, 426, 441 ),
        new CharMetrics( 667, 0, 16, -18, 648, 441 ), new CharMetrics( 444, 0, -27, -11, 447, 441 ),
        new CharMetrics( 444, 0, -24, -206, 426, 441 ), new CharMetrics( 389, 0, -2, -81, 380, 428 ),
        new CharMetrics( 400, 0, 51, -177, 407, 687 ), new CharMetrics( 275, 0, 105, -18, 171, 666 ),
        new CharMetrics( 400, 0, -7, -177, 349, 687 ), new CharMetrics( 541, 0, 40, 183, 502, 323 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 278, 0, 49, -11, 235, 441 ),
        new CharMetrics( 333, 0, 121, 492, 311, 664 ), new CharMetrics( 333, 0, 180, 494, 403, 664 ),
        new CharMetrics( 333, 0, 91, 492, 385, 661 ), new CharMetrics( 333, 0, 100, 517, 427, 624 ),
        new CharMetrics( 333, 0, 99, 532, 411, 583 ), new CharMetrics( 333, 0, 117, 492, 418, 650 ),
        new CharMetrics( 333, 0, 207, 508, 305, 606 ), new CharMetrics( 333, 0, 107, 508, 405, 606 ),
        new CharMetrics( 250, 0, 0, 0, 0, 0 ), new CharMetrics( 333, 0, 155, 492, 355, 691 ),
        new CharMetrics( 333, 0, -30, -217, 182, 0 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 333, 0, 93, 494, 486, 664 ), new CharMetrics( 333, 0, -20, -169, 200, 40 ),
        new CharMetrics( 333, 0, 121, 492, 426, 661 ), new CharMetrics( 250, 0, 0, 0, 0, 0 ),
        new CharMetrics( 389, 0, 59, -205, 322, 473 ), new CharMetrics( 500, 0, 77, -143, 472, 560 ),
        new CharMetrics( 500, 0, 10, -6, 517, 670 ), new CharMetrics( 500, 0, -22, 53, 522, 597 ),
        new CharMetrics( 500, 0, 27, 0, 603, 653 ), new CharMetrics( 275, 0, 105, -18, 171, 666 ),
        new CharMetrics( 500, 0, 53, -162, 461, 666 ), new CharMetrics( 333, 0, 107, 508, 405, 606 ),
        new CharMetrics( 760, 0, 41, -18, 719, 666 ), new CharMetrics( 276, 0, 42, 406, 352, 676 ),
        new CharMetrics( 500, 0, 53, 37, 445, 403 ), new CharMetrics( 675, 0, 86, 108, 590, 386 ),
        new CharMetrics( 333, 0, 49, 192, 282, 255 ), new CharMetrics( 760, 0, 41, -18, 719, 666 ),
        new CharMetrics( 333, 0, 99, 532, 411, 583 ), new CharMetrics( 400, 0, 101, 390, 387, 676 ),
        new CharMetrics( 675, 0, 86, 0, 590, 506 ), new CharMetrics( 300, 0, 33, 271, 324, 676 ),
        new CharMetrics( 300, 0, 43, 268, 339, 676 ), new CharMetrics( 333, 0, 180, 494, 403, 664 ),
        new CharMetrics( 500, 0, -30, -209, 497, 428 ), new CharMetrics( 523, 0, 55, -123, 616, 653 ),
        new CharMetrics( 250, 0, 70, 199, 181, 310 ), new CharMetrics( 333, 0, -30, -217, 182, 0 ),
        new CharMetrics( 300, 0, 43, 271, 284, 676 ), new CharMetrics( 310, 0, 67, 406, 362, 676 ),
        new CharMetrics( 500, 0, 55, 37, 447, 403 ), new CharMetrics( 750, 0, 33, -10, 736, 676 ),
        new CharMetrics( 750, 0, 34, -10, 749, 676 ), new CharMetrics( 750, 0, 23, -10, 736, 676 ),
        new CharMetrics( 500, 0, 28, -205, 368, 471 ), new CharMetrics( 611, 0, -51, 0, 564, 876 ),
        new CharMetrics( 611, 0, -51, 0, 564, 876 ), new CharMetrics( 611, 0, -51, 0, 564, 873 ),
        new CharMetrics( 611, 0, -51, 0, 566, 836 ), new CharMetrics( 611, 0, -51, 0, 564, 818 ),
        new CharMetrics( 611, 0, -51, 0, 564, 883 ), new CharMetrics( 889, 0, -27, 0, 911, 653 ),
        new CharMetrics( 667, 0, 66, -217, 689, 666 ), new CharMetrics( 611, 0, -1, 0, 634, 876 ),
        new CharMetrics( 611, 0, -1, 0, 634, 876 ), new CharMetrics( 611, 0, -1, 0, 634, 873 ),
        new CharMetrics( 611, 0, -1, 0, 634, 818 ), new CharMetrics( 333, 0, -8, 0, 384, 876 ),
        new CharMetrics( 333, 0, -8, 0, 413, 876 ), new CharMetrics( 333, 0, -8, 0, 425, 873 ),
        new CharMetrics( 333, 0, -8, 0, 435, 818 ), new CharMetrics( 722, 0, -8, 0, 700, 653 ),
        new CharMetrics( 667, 0, -20, -15, 727, 836 ), new CharMetrics( 722, 0, 60, -18, 699, 876 ),
        new CharMetrics( 722, 0, 60, -18, 699, 876 ), new CharMetrics( 722, 0, 60, -18, 699, 873 ),
        new CharMetrics( 722, 0, 60, -18, 699, 836 ), new CharMetrics( 722, 0, 60, -18, 699, 818 ),
        new CharMetrics( 675, 0, 93, 8, 582, 497 ), new CharMetrics( 722, 0, 60, -105, 699, 722 ),
        new CharMetrics( 722, 0, 102, -18, 765, 876 ), new CharMetrics( 722, 0, 102, -18, 765, 876 ),
        new CharMetrics( 722, 0, 102, -18, 765, 873 ), new CharMetrics( 722, 0, 102, -18, 765, 818 ),
        new CharMetrics( 556, 0, 78, 0, 633, 876 ), new CharMetrics( 611, 0, 0, 0, 569, 653 ),
        new CharMetrics( 500, 0, -168, -207, 493, 679 ), new CharMetrics( 500, 0, 17, -11, 476, 664 ),
        new CharMetrics( 500, 0, 17, -11, 487, 664 ), new CharMetrics( 500, 0, 17, -11, 476, 661 ),
        new CharMetrics( 500, 0, 17, -11, 511, 624 ), new CharMetrics( 500, 0, 17, -11, 489, 606 ),
        new CharMetrics( 500, 0, 17, -11, 476, 691 ), new CharMetrics( 667, 0, 23, -11, 640, 441 ),
        new CharMetrics( 444, 0, 26, -217, 425, 441 ), new CharMetrics( 444, 0, 31, -11, 412, 664 ),
        new CharMetrics( 444, 0, 31, -11, 459, 664 ), new CharMetrics( 444, 0, 31, -11, 441, 661 ),
        new CharMetrics( 444, 0, 31, -11, 451, 606 ), new CharMetrics( 278, 0, 49, -11, 284, 664 ),
        new CharMetrics( 278, 0, 49, -11, 356, 664 ), new CharMetrics( 278, 0, 34, -11, 328, 661 ),
        new CharMetrics( 278, 0, 49, -11, 353, 606 ), new CharMetrics( 500, 0, 27, -11, 482, 683 ),
        new CharMetrics( 500, 0, 14, -9, 476, 624 ), new CharMetrics( 500, 0, 27, -11, 468, 664 ),
        new CharMetrics( 500, 0, 27, -11, 487, 664 ), new CharMetrics( 500, 0, 27, -11, 468, 661 ),
        new CharMetrics( 500, 0, 27, -11, 496, 624 ), new CharMetrics( 500, 0, 27, -11, 489, 606 ),
        new CharMetrics( 675, 0, 86, -11, 590, 517 ), new CharMetrics( 500, 0, 28, -135, 469, 554 ),
        new CharMetrics( 500, 0, 42, -11, 475, 664 ), new CharMetrics( 500, 0, 42, -11, 477, 664 ),
        new CharMetrics( 500, 0, 42, -11, 475, 661 ), new CharMetrics( 500, 0, 42, -11, 479, 606 ),
        new CharMetrics( 444, 0, -24, -206, 459, 664 ), new CharMetrics( 500, 0, -75, -205, 469, 683 ),
        new CharMetrics( 444, 0, -24, -206, 441, 606 )};

    SerifItalic()
    {
        super( false, 683, -205, new CharMetrics( 0, 0, -169, -217, 1010, 883 ), metrics );
    }
}
